package com.sdk.satwatch.util;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.regex.Pattern;

import android.annotation.TargetApi;
//import CommonClass.UI.BackgroundScaleDrawable;
//import CommonClass.UI.MoreDrawable;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Message;
import android.text.SpannableString;
import android.text.format.Time;
import android.util.Size;
import android.view.View;

/**
 * 通用功能类型
 * @author tcb
 *
 */
public class CommonFuc {
	
	/**
	 * 正则表达式匹配
	 * @param pattern  正则表达试
	 * @param input  要匹配的字符串
	 * @return
	 */
    public static boolean IsMacth(String  pattern,String input){
    	Pattern P=Pattern.compile(pattern);
    	return P.matcher(input).matches();
    }
    
    
   /**
    * 计算页面数量
    * @param num  记录总数量
    * @param pagesize  页面大小
    * @return  返回页面数量
    */
    public static int GetPageCount(int num,int pagesize){
    	if (num>0 && pagesize>0){
    		if (num%pagesize!=0){
    			return (num/pagesize)+1;
    		}
    		else{
    			return num/pagesize;
    		}
    	}
    	return 1;
    }
    
    
    /**
     * 获取当前日期
     * @return
     */
    public static  String  NowDateTime(){
    	Time time = new Time(Time.getCurrentTimezone());
    	time.setToNow();
    	int month=time.month+1;
    	return time.year+"-"+month+"-"+time.monthDay+" "+time.hour+":"+time.minute+":"+time.second;
    }

    
    /**
     * 去掉号码中空字符串
     * @param num
     * @return
     */
    public static String makePhoneNumTrue(String num){
    	String R="";
    	if (num!=null && (num.length())>0){
           for (int i=0;i<num.length();i++){
        	   String curr=num.substring(i,i+1);
        	   if (!curr.equals(" ")){
        		   R+=curr;
        	   }
           }
    	}
    	return R;
    }

    /**
     * 将文本形数字转换为整型
     * @param num  文本形数字
     * @return
     */
    public static int ConvertStringToInt(String num){
    	try{
    		return Integer.parseInt(num);
    	}catch(Exception ex){
    		return 0;
    	}
    }
    
    
    
    /**
     * 生成遮罩后的图形
     * @param bmpa  原始图像
     * @param mask 遮罩图像
     * @return  遮罩后的图像
     */
    public static Bitmap maskBitmap(int bmpw, Bitmap bmpa,Bitmap mask){
    	Bitmap bmp=null;
    	if (bmpa!=null){
    		bmp=Bitmap.createBitmap(bmpw, bmpw, Config.ARGB_8888);
    	    Canvas  can=new Canvas(bmp);
    	    Paint p=new Paint();
    	    p.setAntiAlias(true);
    	    can.drawBitmap(bmpa, new Rect(0,0,bmpa.getWidth(),bmpa.getHeight()), new Rect(0,0,bmp.getWidth(),bmp.getHeight()), p);
    	    bmpa=null;
    	   // can.setBitmap(null);
    	}
    	if (bmp!=null && mask!=null){
    		int bmpW=bmp.getWidth();
    		int bmpH=bmp.getHeight();
    		int maskW=mask.getWidth();
    		int maskH=mask.getHeight();
    	 if (bmpW>0 && bmpH>0 && maskW>0 && maskH>0){
    		 int[] bmppixel=new int[bmpW*bmpH];
    		 bmp.getPixels(bmppixel, 0, bmpW, 0, 0, bmpW,bmpH);
    		 int maskpixel[]=new int[maskW*maskH];
    		 int kk=0;
    		 mask.getPixels(maskpixel, 0, maskW, 0, 0, maskW,maskH);
    		 for (int i=0;i<bmpH;i++){
    			 for(int w=0;w<bmpW;w++){
    				 if (i<maskH && w<maskW){
    					 int curBmp=i*bmpW+w;
    					 int curMask=i*maskW+w;
    					 kk=maskpixel[curMask]&0x000000FF;
    					 int oldalpha=((bmppixel[curBmp]>>24)& 0x000000FF);
    					 kk=((kk*oldalpha)/255);
    					 kk=(kk<<24);
    					 bmppixel[curBmp]=kk+(bmppixel[curBmp] & 0x00FFFFFF);
    				 }
    			 }
    		 }
    		 bmp.setPixels(bmppixel, 0, bmpW, 0, 0, bmpW,bmpH);
    	 }
    	
    	}
    	return bmp;
    }
    
    /**
     * 判断数字字符是否正整数
     * @param num  字符型数字
     * @return
     */
    public static boolean StringIsRealInt(String num){
    	try{
    		int i=Integer.parseInt(num);
    		if (i>=0){
    			return true;
    		}else{
    			return false;
    		}
    	}catch(Exception ex){
    		return false;
    	}
    }
    
    /**
     * 判断数字字符是否正Double型
     * @param num 字符型数字
     * @return
     */
    public static boolean StringIsRealDouble(String num){
    	try{
    		double i=Double.parseDouble(num);
    		if (i>=0.0){
    			return true;
    		}else{
    			return false;
    		}
    	}catch(Exception ex){
    		return false;
    	}
    }

	/**
	 * 获取启动icon
	 * @param context
	 * @return
	 */
	public  static  Bitmap  getLaunchIcon(Context context){
		    ApplicationInfo  applicationInfo= context.getApplicationInfo();
		    if (applicationInfo!=null){
		        int launchIcon = applicationInfo.icon;
		        if (launchIcon>0){
		        	try {
						return  BitmapFactory.decodeResource(context.getResources(),launchIcon);
					}catch (Exception exception){}
				}
			}
		    return null;
	}

	/**
	 * 获取app名称
	 * @param context
	 * @return
	 */
	public  static  CharSequence  getAppName(Context context){
		ApplicationInfo  applicationInfo= context.getApplicationInfo();
		if (applicationInfo!=null){
			CharSequence  charSequence= context.getPackageManager().getApplicationLabel(applicationInfo);
			if (charSequence==null){
				charSequence=new SpannableString("");
			}
			return charSequence;
		}
		return "";
	}

	/**
	 * 压缩图片
	 * @param bmp
	 * @param format
	 * @param quality
	 * @return
	 */
	public static   byte[]  compressBitmap(Bitmap  bmp, Bitmap.CompressFormat format,int quality) {
		if (bmp!=null) {
			ByteArrayOutputStream byteArrayOutputStream = null;
			try {
				byteArrayOutputStream = new ByteArrayOutputStream();
                bmp.compress(format,quality,byteArrayOutputStream);
				byte[] buff=byteArrayOutputStream.toByteArray();
				byteArrayOutputStream.close();
				return buff;
			} catch (Exception exception) {
               if (byteArrayOutputStream!=null){
				   try {
					   byteArrayOutputStream.close();
				   } catch (IOException e) {
					   e.printStackTrace();
				   }
			   }
			}
		}
		return new byte[0];
	}
    
    /**
     * 获取图像字节流
     * @param bmp 图像
     * @return
     */
    public static  byte[] GetBitmapBytes(Bitmap bmp){
    	  if (bmp!=null){
    		  ByteArrayOutputStream bs=null;
    		  try{
    		      bs=new ByteArrayOutputStream();
    		      bmp.compress(Bitmap.CompressFormat.PNG, 70, bs);
    		      bs.close();
    		      byte[] R=bs.toByteArray();
    		      return R;
    		  }catch(Exception ex){
    			  if (bs!=null){
    				try {
						bs.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
    			  }
    		  }
    	  }
    	  return null;
    }

	/**
	 * 旋转图像90度
	 * @param bmp
	 * @return
	 */
	public  static  Bitmap RotateBitmap(Bitmap  bmp,int angle){
		     if (bmp!=null){
		     	 if ((bmp.getWidth()>0) && (bmp.getHeight()>0)){
					 Bitmap newbmp =null;
		     	 	 if ((angle==90) || (angle==270)) {
						 newbmp = Bitmap.createBitmap(bmp.getHeight(), bmp.getWidth(), Config.ARGB_8888);
					 }else {
						 newbmp = Bitmap.createBitmap(bmp.getWidth(),bmp.getHeight(), Config.ARGB_8888);
					 }
					 Canvas  canvas=new Canvas(newbmp);
					 canvas.rotate(angle,bmp.getWidth()/2,bmp.getHeight()/2);
					// canvas.translate(0,-newbmp.getWidth());
					 canvas.drawBitmap(bmp,0,0,null);
					 bmp.recycle();
					 return newbmp;
				 }
			 }
    	     return bmp;
	}
    
    
    /**
	 * 判断是否在后台
	 * @param context
	 * @return
	 */
	public final static boolean isBackground(Context context) {
	     ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
	     List<RunningTaskInfo> appProcesses = activityManager.getRunningTasks(1);
	     if (appProcesses.size()>0){
	    	 ComponentName cpName=appProcesses.get(0).topActivity;
	    	 if (cpName!=null){
	    		if (context.getPackageName().equals(cpName.getPackageName())){
	    			return false;
	    		}
	    	 }
	     }
	     return true;
	}
	

    
    
    /**
     * 从sd磁盘中获取图像
     * @param picname 图片名称
     * @return
     */
    public static Bitmap GetPicFromStorege(Context context,String dirName,String picname){
    	if (ExistsFile(context,dirName, picname)){
    		//Log.e("GetBitmap", GetBitmapPath(picname));
    		try
    		{
    		   return BitmapFactory.decodeFile(GetBitmapPath(context,dirName,picname));
    		}catch(Exception e){
    			return null;
    		}
    		
    	}
    	return null;
    }


	/**
	 * 获取图像缩略图
	 * @param dirName
	 * @param picname
	 * @param wd
	 * @param ht
	 * @return
	 */
	public static Bitmap getSmallBmpFromStorege(Context context,String dirName,String picname,int wd,int ht){
  
  	    InputStream IS=null;
  	    Bitmap bmp=null;
  	    if (ExistsFile(context,dirName, picname)) {
  	    	try{
  	  	    	String path=GetBitmapPath(context,dirName,picname);
  	  	    	 IS=new FileInputStream(path);
  				 BitmapFactory.Options opt=new BitmapFactory.Options ();
  				 opt.inJustDecodeBounds=true;
  				 bmp=BitmapFactory.decodeStream(IS, null, opt);
  				 IS.close();
  				 opt.inJustDecodeBounds=false;
  	 			opt.inSampleSize=1;
  	 			int w=opt.outWidth/wd;
  	 			int h=opt.outHeight/ht;
  	 			if (w>=h && w>=1){
  	 				opt.inSampleSize=w;
  	 			}else{
  	 			   if (h>=w && h>=1){
  	 				   opt.inSampleSize=h;	
  	 				}
  	 			 }
  	 		
  	 			IS=new FileInputStream(path);
  	 			bmp=BitmapFactory.decodeStream(IS, null, opt);
  	 			IS.close();
  	  	    }catch(Exception e){
  	  	    	if (bmp!=null){
  	   			     bmp.recycle();
  	   			     bmp=null;
  	   		     } 
  	   		  
  	   		     if (IS!=null){
  	   			    try {
  						IS.close();
  					 } catch (IOException e1) {
  						// TODO Auto-generated catch block
  						e1.printStackTrace();
  					 }
  	   		     }
  	  	    }
  	    }
    	return bmp;
    }


	/**
	 * 获取图像缩略图
	 * @param path
	 * @param wd
	 * @param ht
	 * @return
	 */
	public static Bitmap getSmallBmpFromStorege(String path,int wd,int ht){
  	    InputStream IS=null;
  	    Bitmap bmp=null;
  	    try{
  	    	 IS=new FileInputStream(path);
			 BitmapFactory.Options opt=new BitmapFactory.Options ();
			 opt.inJustDecodeBounds=true;
			 bmp=BitmapFactory.decodeStream(IS, null, opt);
			 IS.close();
			 opt.inJustDecodeBounds=false;
 			opt.inSampleSize=1;
 			int w=opt.outWidth/wd;
 			int h=opt.outHeight/ht;
 			if (w>=h && w>=1){
 				opt.inSampleSize=w;
 			}else{
 			   if (h>=w && h>=1){
 				   opt.inSampleSize=h;	
 				}
 			 }
 		
 			IS=new FileInputStream(path);
 			bmp=BitmapFactory.decodeStream(IS, null, opt);
 			IS.close();
  	    }catch(Exception e){
  	    	if (bmp!=null){
   			 bmp.recycle();
   			 bmp=null;
   		 } 
   		  
   		 if (IS!=null){
   			 try {
					IS.close();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
   		 }
  	    }
    	return bmp;
    } 
    
    
    /**
     * 获取图像缩略图
     * @param mcon  程序上下文
     * @param path  图像路径
     * @return
     */
    public static Bitmap getSmallBmp(Context mcon,String path){
    	ContentResolver CR=null;
  	    InputStream IS=null;
  	    Bitmap bmp=null;
  	    try{
  	    	Uri u=Uri.parse(path);
    		CR=mcon.getContentResolver(); 
    		 IS=CR.openInputStream(u);
			 BitmapFactory.Options opt=new BitmapFactory.Options ();
			 opt.inJustDecodeBounds=true;
			 bmp=BitmapFactory.decodeStream(IS, null, opt);
			 IS.close();
			 opt.inJustDecodeBounds=false;
 			opt.inSampleSize=1;
 			int w=opt.outWidth/50;
 			int h=opt.outHeight/50;
 			if (w>=h && w>=1){
 				opt.inSampleSize=w;
 			}else{
 			   if (h>=w && h>=1){
 				   opt.inSampleSize=h;	
 				}
 			 }
 		
 			IS=CR.openInputStream(u);
 			bmp=BitmapFactory.decodeStream(IS, null, opt);
 			IS.close();
  	    }catch(Exception e){
  	    	if (bmp!=null){
   			 bmp.recycle();
   			 bmp=null;
   		 } 
   		  
   		 if (IS!=null){
   			 try {
					IS.close();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
   		 }
  	    }
    	return bmp;
    } 
    
    
    /**
     * 删除指定的图片文件
     * @param dirName
     * @param picname
     */
    public static void deleteBitmapFile(Context context,String dirName,String picname){
    	if (hasSdCard()){
    		try {
     		   File F=new File(GetBitmapPath(context,dirName,picname));
     		  if (F.exists()){
     			  F.delete();
     		  }
    		}catch (Exception e) {
				// TODO: handle exception
			}
    	}
    }
    
    /**
     * 指定的文件是否存在
     * @param dirName
     * @param picname
     */
    public static boolean ExistsFile(Context context,String dirName,String picname){
    	if (hasSdCard()){
    		try {
     		   File F=new File(GetBitmapPath(context,dirName,picname));
     		  if (F.exists()){
     			 return true;
     		  }
    		}catch (Exception e) {
				// TODO: handle exception
			}
    	}
    	return false;
    }
    
    
    /**
     * 保存图像
     * @param picname 图片名称
     * @param bmp  图像
     * @return  返回存储的path
     */
    public static String SaveBitmap(Context context,String dirName,String picname,Bitmap bmp){
    	if (bmp==null){
    		return "";
    	}
    	if (hasSdCard()){
    		try {
    		   File F=new File(GetBitmapPath(context,dirName,picname));
    		   //Log.e("SaveBitmap", GetBitmapPath(picname));
    		   if (!F.exists()){
    			   F.createNewFile();
    		   }
    		   FileOutputStream FOS=new FileOutputStream(F, false);
			  // Log.e("SaveBitmap", GetBitmapPath(picname));
    		   bmp.compress(Bitmap.CompressFormat.PNG, 100, FOS);
    		   FOS.close();
    		   return F.getPath();
    		}catch(Exception e){
    			// Log.e("eeSaveBitmap",e.getMessage());
    			e.printStackTrace();
    		}
    	}
    	return "";
    }
    
    /**
     * 保存文件数据
     * @param input
     * @param dirName
     * @param picname
     */
    public static  void SaveFile(Context context,InputStream input,String dirName,String picname) {
    	if (hasSdCard()){
    		FileOutputStream FOS=null;
    		try {
     		   File F=new File(GetBitmapPath(context,dirName,picname));
     		   //Log.e("SaveBitmap", GetBitmapPath(picname));
     		   if (!F.exists()){
     			   F.createNewFile();
     		   }
     		   FOS=new FileOutputStream(F, false);
     		   byte[] B=new byte[1024*32];
     		   int rd=0;
     		   while(true){
     		      rd=input.read(B);
     		      if (rd<0){
     		    	  break;
     		      }
     		      if (rd>0){
     		    	  FOS.write(B, 0, rd);
     		      }
     		   }
     		   FOS.close();
     		   
     		}catch(Exception e){
     			// Log.e("eeSaveBitmap",e.getMessage());
     			if (FOS!=null){
     				try {
						FOS.close();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}     	
     			}
     			try {
     				File F=new File(GetBitmapPath(context,dirName,picname));
     				if (F.exists()){
     					F.delete();
     				}
     			}catch (Exception e2) {
					// TODO: handle exception
				}
     		    
       		   //Log.e("SaveBitmap", GetBitmapPath(picname));
       		   
     			e.printStackTrace();
     		}
    	}
    }
    
    
    /**
     * 是否有SdCard
     * @return
     */
    public static boolean hasSdCard(){
    	if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
    		return true;
    	}
    	return false;
    }
    
    
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)  
    public static void  setViewLayerTypeSoftware(View v) {
    	if (Build.VERSION.SDK_INT>=11) {
			v.setLayerType(View.LAYER_TYPE_SOFTWARE,null);	
    	}
    }
    
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void setViewElevation(View v,float elevation) {
    	if (Build.VERSION.SDK_INT>=21) {
    	      v.setElevation(elevation);
    	      v.setTranslationZ(elevation);
    	}
    }
    
    /**
     * String 转Double
     * @param num
     * @return
     */
    public static double ConvertDoubleFromString(String num){
    	try{
    	   return Double.parseDouble(num);
    	}catch(Exception e){
    		return 0.0;
    	}
    } 
    
    /**
     * String 转Long
     * @param num
     * @return
     */
    public static long ConvertLongFromString(String num){
    	try{
    	   return Long.parseLong(num);
    	}catch(Exception e){
    		return 0;
    	}
    } 
    
    
    public static float ConvertFloatFromString(String num){
    	try{
    	   return Float.parseFloat(num);
    	}catch(Exception e){
    		return 0.0f;
    	}
    } 
    
    
    /**
     * 生成圆形遮罩图
     * @param con 应用程序上下文
     * @param WDip 图像长宽大小（正方形）
     * @param obmp 原图像
     * @return
     */
    public static Bitmap  makeMaskCircleBmp(Context con,int WDip,Bitmap obmp){
    	int width=diptopx(con, WDip);
    	Bitmap  bmp=Bitmap.createBitmap(width, width, Config.ARGB_8888);
    	Canvas  can=new Canvas(bmp);
    	Paint  p=new Paint();
    	p.setAntiAlias(true);
    	p.setStyle(Style.FILL);
    	p.setColor(Color.BLACK);
    	can.drawRect(new Rect(0,0,width,width), p);
    	p.setColor(Color.WHITE);
    	can.drawCircle(width/2, width/2, width/2, p);
    	//can.setBitmap(null);
    	//can.setBitmap(obmp);
    	//p.setStyle(Style.STROKE);
    	//p.setStrokeWidth(2);
    	//p.setColor(Color.argb(255, 190, 197, 201));
    	//if ((width-2)>0){
    	  // can.drawCircle(width/2, width/2, (width-2)/2, p);
    	//}
    	Bitmap R=maskBitmap(width, obmp, bmp);
    	if (bmp!=null){
    		bmp.recycle();
    	}
    	bmp=Bitmap.createBitmap(1, 1, Config.ARGB_8888);
    	can.setBitmap(bmp);
    	if (bmp!=null){
    		bmp.recycle();
    	}
    	return R;
    }
    
    
    /**
     * 生成圆形遮罩图
     * @param con 应用程序上下文
     * @param rwdip 圆角大小
     * @param obmp 原图像
     * @return
     */
    public static Bitmap  makeMaskRoundRectBmp2(Context con,int rwdip,Bitmap obmp){
    	int rdw=diptopx(con, rwdip);
    	Bitmap  bmp=Bitmap.createBitmap(obmp.getWidth(), obmp.getHeight(), Config.ARGB_8888);
    	Canvas  can=new Canvas(bmp);
    	Paint  p=new Paint();
    	p.setShader(new BitmapShader(obmp, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));
    	p.setAntiAlias(true);
    	p.setStyle(Style.FILL);
    	can.drawRoundRect(new RectF(0,0,bmp.getWidth(),bmp.getHeight()), (float)rdw,(float)rdw, p);
    	p.setShader(new Shader());
    	can.setBitmap(Bitmap.createBitmap(1, 1, Config.ARGB_8888));
        return bmp;
    }
    
    
    /**
     * 裁剪bmp
     * @param con
     * @param obmp
     * @param range
     * @return
     */
    public static Bitmap  ClipBmp(Context con,Bitmap obmp,int range){
    	int rd=diptopx(con, range);
    	int w=rd;
    	int h=rd;
    	int wd=0;
    	int x=0;
    	int y=0;
    	if (obmp.getHeight()>obmp.getWidth()){
    		wd=obmp.getWidth();
    		x=0;
    		y=(obmp.getHeight()-wd)/2;
    	}else{
    		wd=obmp.getHeight();
    		x=(obmp.getWidth()-wd)/2;
    		y=0;
    	}
    	
    	Bitmap  bmp=Bitmap.createBitmap(w, h, Config.ARGB_8888);
    	Canvas  can=new Canvas(bmp);
    	can.drawBitmap(obmp,new Rect(x, y, x+wd, y+wd),new Rect(0, 0, w, h),null);
    	can.setBitmap(Bitmap.createBitmap(1, 1, Config.ARGB_8888));
    	obmp.recycle();
        return bmp;
    }
    
    
   
    
    
    /**
     * 生成圆形遮罩图
     * @param con 应用程序上下文
     * @param WDip 图像长宽大小（正方形）
     * @param obmp 原图像
     * @return
     */
    public static Bitmap  makeMaskRoundRectBmp(Context con,int WDip,int rwdip,Bitmap obmp){
    	int width=diptopx(con, WDip);
    	int rdw=diptopx(con, rwdip);
    	Bitmap  bmp=Bitmap.createBitmap(width, width, Config.ARGB_8888);
    	Canvas  can=new Canvas(bmp);
    	Paint  p=new Paint();
    	p.setAntiAlias(true);
    	p.setStyle(Style.FILL);
    	p.setColor(Color.BLACK);
    	can.drawRect(new Rect(0,0,width,width), p);
    	p.setColor(Color.WHITE);
    	can.drawRoundRect(new RectF(0,0,width,width), (float)rdw,(float)rdw, p);
    	//can.setBitmap(null);
    	//can.setBitmap(obmp);
    	//p.setStyle(Style.STROKE);
    	//p.setStrokeWidth(2);
    	//p.setColor(Color.argb(255, 190, 197, 201));
    	//if ((width-2)>0){
    	  // can.drawCircle(width/2, width/2, (width-2)/2, p);
    	//}
    	Bitmap R=maskBitmap(width, obmp, bmp);
    	if (bmp!=null){
    		bmp.recycle();
    	}
    	bmp=Bitmap.createBitmap(1, 1, Config.ARGB_8888);
    	can.setBitmap(bmp);
    	if (bmp!=null){
    		bmp.recycle();
    	}
    	return R;
    }
    
    
    /**
     * dp度量单位到px的转换
     * @param context  程序资源上下文
     * @param dipValue  dip值
     * @return
     */
    public static int diptopx(Context context, float dipValue){ 
        final float scale = context.getResources().getDisplayMetrics().density; 
        return (int)(dipValue * scale + 0.5f); 
    }

	/**
	 * 将sp值转换为px值，保证文字大小不变
	 * @param context
	 * @param spValue
	 *
	 *            （DisplayMetrics类中属性scaledDensity）
	 * @return
	 */
	public static int sp2px(Context context, float spValue) {
		final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
		return (int) (spValue * fontScale + 0.5f);
	}


	public static int getDisplayWidth(Context con){
    	return con.getResources().getDisplayMetrics().widthPixels;
    }
    
    public static int getDisplayHeight(Context con){
    	return con.getResources().getDisplayMetrics().heightPixels;
    }
    
    /**
     * px度量单位到dp的转换
     * @param context  程序资源上下文
     * @param pxValue  px值
     * @return
     */
    public static int pxtodip(Context context, float pxValue){ 
        final float scale = context.getResources().getDisplayMetrics().density; 
        if (scale>0){
          return (int)((pxValue-0.5f)/scale); 
        }else{
        	return (int)pxValue;
        }
    } 


    /**
     * 检测指定的图片文件是否存在
     * @param picname 图片名称
     * @return 
     */
    private static boolean IsExistFile(String picname){
    	if (hasSdCard()){
    		String DirPath=Environment.getExternalStorageDirectory().getAbsolutePath()+"/handpayer/";
    		//Log.e("Path",DirPath);
    		File dir=new File(DirPath);
    		if (!dir.exists()){
    			dir.mkdir();
    		}
    		File F=new File(DirPath+picname);
    		return F.exists();
    	}
    	return false;
    }
    
    /**
     * 获取指定图片名称的路径
     * @param picname  图片名称
     * @return
     */
    public static String GetBitmapPath(Context context,String dirName,String picname){
    	if (hasSdCard()){
			String DirPath =context.getExternalFilesDir(null) + "/"+dirName+"/";// Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + dirName + "/";
    		if (Build.VERSION.SDK_INT>=29) {
				  DirPath =  context.getExternalFilesDir(null) + "/"+dirName+"/";
			}
    		File dir=new File(DirPath);
    		if (!dir.exists()){
    			if (!dir.mkdir()){
    				return "";
    			}
    		}
    		return DirPath+picname;
    	}
    	return "";
    } 
    
    
    /**
     * 从流中读取字符串
     * @param IS
     * @return
     */
    public static String GetStringFromStream(InputStream IS) throws Exception{
    	StringBuilder  SB=new StringBuilder();
    	int total=0;//总字符数
		int maxlen=1024*1024*4;
    	try {
			InputStreamReader SR=new InputStreamReader(IS, "utf-8");
			BufferedReader SS=new  BufferedReader(SR);
			try {
				char[] bf=new char[1024];
				while (true)
				{
					int i=SS.read(bf, 0, 1024);
					if (i<0)
					{
						break;
					}
					else
					{
						SB.append(bf,0,i);
					}
					total+=i;
					if (total>maxlen){
						 throw  new Exception("字符串解析错误，总字符数不得超过4M");
					}
				}
				
			}catch (Exception e){
				if (SS!=null){
					SS.close();
				}
				if (SR!=null){
					SR.close();
				}
				throw  e;
			}
			SS.close();
			SR.close();
		}catch (Exception e){
			throw  e;
		}
		return SB.toString();
    }
}
