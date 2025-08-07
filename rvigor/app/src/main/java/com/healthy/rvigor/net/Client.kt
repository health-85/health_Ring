package com.healthy.rvigor.net

import android.text.TextUtils
import com.healthbit.framework.FrameworkConfig
import com.healthbit.framework.base.BaseApplication
import com.healthy.rvigor.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.xutils.common.util.LogUtil
import retrofit2.CallAdapter
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.fastjson.FastJsonConverterFactory
import java.io.IOException
import java.io.InputStream
import java.security.KeyStore
import java.security.KeyStoreException
import java.security.NoSuchAlgorithmException
import java.security.SecureRandom
import java.security.UnrecoverableKeyException
import java.security.cert.CertificateException
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import java.util.Arrays
import java.util.concurrent.TimeUnit
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.KeyManager
import javax.net.ssl.KeyManagerFactory
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.TrustManager
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager

/**
 * @Description:    Rvigor
 * @Author:         wb
 * @CreateDate:     2024/5/6 10:25
 * @UpdateRemark:
 */
class Client {

    private val mServiceMap = HashMap<String, Any>()

    private val fastJsonConverterFactory: Converter.Factory = FastJsonConverterFactory.create()
    private val rxJavaCallAdapterFactory: CallAdapter.Factory = RxJavaCallAdapterFactory.create()

    private val DEFAULT_TIMEOUT = 35

    private val DEFAULT_UPLOAD_TIMEOUT = 60

    private fun createService(serviceClass: Class<ApiService>, baseUrl: String): Any {
        val mRetrofitClient = generateRetrofitClient(baseUrl)
        return mRetrofitClient.create(serviceClass)
    }

    companion object {
        val sIntance by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            Client()
        }
    }

    fun getService(serviceClass: Class<ApiService>, baseUrl: String): Any {
        return (if (mServiceMap.containsKey(serviceClass.name)) {
            mServiceMap[serviceClass.name]
        } else {
            val obj: Any = createService(serviceClass, baseUrl)
            mServiceMap[serviceClass.name] = obj
            obj
        }) as Any
    }

    private fun generateRetrofitClient(baseUrl: String?): Retrofit {
        val logInterceptor = HttpLoggingInterceptor(LoggingInterceptor())
        logInterceptor.level = HttpLoggingInterceptor.Level.BODY
        val builder = OkHttpClient.Builder()
        if (FrameworkConfig.getConfig().useUnsafeHttps) {
            getX509TrustManager()?.let { builder.sslSocketFactory(getSSLSocketFactory()!!, it) }
            builder.hostnameVerifier(getHostnameVerifier()!!)
        } else if (FrameworkConfig.getConfig().sslCertPath != null) {
            getX509TrustManager()?.let {
                builder.sslSocketFactory(getCertificateFactory(*FrameworkConfig.getConfig().sslCertPath)!!,
                    it
                )
            }
        }
        builder.connectTimeout(DEFAULT_TIMEOUT.toLong(), TimeUnit.SECONDS)
        builder.readTimeout(DEFAULT_TIMEOUT.toLong(), TimeUnit.SECONDS)
        builder.writeTimeout(DEFAULT_UPLOAD_TIMEOUT.toLong(), TimeUnit.SECONDS)
        builder.addInterceptor(logInterceptor)
        builder.retryOnConnectionFailure(true)
        // 缓存设置为 100Mb
        //builder.cache(new Cache(new File(App.getInstance().getCacheDir(), FrameworkConfig.getConfig().cacheDir), 1024 * 1024 * 100));
        val mApiClient = builder.build()
        return Retrofit.Builder()
            .client(mApiClient)
            .baseUrl(baseUrl)
            .addConverterFactory(fastJsonConverterFactory)
            .addCallAdapterFactory(rxJavaCallAdapterFactory)
            .build()
    }

    private val TAG = "Client"

    private class LoggingInterceptor : HttpLoggingInterceptor.Logger {
        override fun log(message: String) {
            if (BuildConfig.DEBUG && !TextUtils.isEmpty(message)) {
                if (!message.contains("base64_image")) {
                    //字符床太长了.不显示
                    LogUtil.d(message)
                }
            }
        }
    }

    private fun getSSLSocketFactory(): SSLSocketFactory? {
        return try {
            val sslContext = SSLContext.getInstance("SSL")
            sslContext.init(null, getTrustManager(), SecureRandom())
            sslContext.socketFactory
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

    private fun getTrustManager(): Array<TrustManager>? {
        return arrayOf(
            object : X509TrustManager {
                override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {}
                override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {}
                override fun getAcceptedIssuers(): Array<X509Certificate> {
                    return arrayOf()
                }
            }
        )
    }

    private fun getX509TrustManager(): X509TrustManager? {
        var trustManager: X509TrustManager? = null
        try {
            val trustManagerFactory =
                TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
            var ks: KeyStore? = null
            trustManagerFactory.init(ks)
            val trustManagers = trustManagerFactory.trustManagers;
            if (trustManagers.size != 1 || !(trustManagers[0] is X509TrustManager)) {
                throw IllegalStateException ("Unexpected default trust managers:" + Arrays.toString(
                    trustManagers
                ))
            }
            trustManager = trustManagers[0] as X509TrustManager?
        } catch (e: Exception) {
            e.printStackTrace();
        }
        return trustManager
    }

    private fun getHostnameVerifier(): HostnameVerifier? {
        return HostnameVerifier { s, sslSession -> true }
    }

    private fun prepareKeyManager(bksFile: InputStream?, password: String?): Array<KeyManager?>? {
        try {
            if (bksFile == null || password == null) {
                return null
            }
            val clientKeyStore = KeyStore.getInstance("BKS")
            clientKeyStore.load(bksFile, password.toCharArray())
            val keyManagerFactory =
                KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm())
            keyManagerFactory.init(clientKeyStore, password.toCharArray())
            return keyManagerFactory.keyManagers
        } catch (e: KeyStoreException) {
            e.printStackTrace()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        } catch (e: UnrecoverableKeyException) {
            e.printStackTrace()
        } catch (e: CertificateException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    private fun getCertificateFactory(vararg certificatePaths: String?): SSLSocketFactory? {
        try {
            val certificateFactory = CertificateFactory.getInstance("X.509")
            val keyStore = KeyStore.getInstance(KeyStore.getDefaultType())
            keyStore.load(null)
            for (i in certificatePaths.indices) {
                val certificateAlias = Integer.toString(i + 1)
                val certificatePath = certificatePaths[i]
                val certificate = BaseApplication.getContext().assets.open(
                    certificatePath!!
                )
                keyStore.setCertificateEntry(
                    certificateAlias,
                    certificateFactory.generateCertificate(certificate)
                )
                try {
                    if (certificate != null) {
                        certificate.close()
                    }
                } catch (e: IOException) {
                }
            }
            val sslContext = SSLContext.getInstance("TLS")
            val trustManagerFactory =
                TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
            trustManagerFactory.init(keyStore)
            sslContext.init(
                null,
                trustManagerFactory.trustManagers, SecureRandom()
            )
            return sslContext.socketFactory
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }
}