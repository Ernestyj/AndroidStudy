package com.androidstudy.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.widget.TextView;
import android.widget.Toast;

import com.androidstudy.R;

/**DataPath extends Activity(Includes many static methods)
 * Test: getFilesDir(), getCacheDir(), Environment.getDataDirectory(),
 * 		Environment.getExternalStorageDirectory()
 * @author Eugene
 * @date 2014-12-3
 */
public class DataPath extends Activity{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		// /data/data/包名/files/
		File filesDir = this.getFilesDir();
		// /data/data/包名/cache
		File cacheDir = this.getCacheDir();
		//获取手机内部存储路径
		File dataDirectory = Environment.getDataDirectory();
		//获取SD路径，写入权限 <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
		//4.0前不需读入权限，4.0后需读入权限 <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"/>
		File externalStorageDirectory = Environment.getExternalStorageDirectory();
		File rootDirectory = Environment.getRootDirectory();
		
		TextView textView = (TextView) findViewById(R.id.txView);
		textView.setText("context.getFilesDir(): " + filesDir 
				+ "\n\n" + "context.getCacheDir(): " + cacheDir 
				+ "\n\n" + "Environment.getExternalStorageDirectory(): " + externalStorageDirectory
				+ "\n\n" + "Environment.getExternalStorageDirectory().getPath(): " + externalStorageDirectory.getPath()
				+ "\n\n" + "Environment.getRootDirectory(): " + rootDirectory
				+ "\n\n" + "Environment.getRootDirectory().getPath(): " + rootDirectory.getPath()
				+ "\n\n" + "Environment.getRootDirectory().getAbsolutePath(): " + rootDirectory.getAbsolutePath()
				+ "\n\n" + "Environment.getDataDirectory(): " + dataDirectory
				+ "\n\n" + "IsSDMounted(): " + IsSDMounted()
				+ "\n\n" + "GetMemoryInfo(externalStorageDirectory): " + GetMemoryInfo(this, externalStorageDirectory)
				+ "\n\n" + "GetMemoryInfo(dataDirectory): " + GetMemoryInfo(this, dataDirectory)
				);
	}

	/**判断是否安装SD卡
	 * @return if SD is mounted
	 */
	public static boolean IsSDMounted() {
		//获取SD状态
		String state = Environment.getExternalStorageState();
		if(Environment.MEDIA_MOUNTED.equals(state)){
			return true;
		}else {
			return false;
		}
	}
	
	/**根据路径获取存储信息
	 * @param context
	 * @param path
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static String GetMemoryInfo(Context context, File path){
		//获取磁盘状态对象
		StatFs stat = new StatFs(path.getPath());
		//获取一个扇区的大小
		int blockSize = stat.getBlockSize();
		//获取扇区总数
		int blockCount = stat.getBlockCount();
		//获取可用扇区数量
		int availableBlocks = stat.getAvailableBlocks();
		//总空间
        String totalMemory =  Formatter.formatFileSize(context, blockCount * blockSize);
        //可用空间
        String availableMemory = Formatter.formatFileSize(context, availableBlocks * blockSize);
		
		return "\n   总空间: " + totalMemory + "\n   可用空间: " + availableMemory;
	}
	
	/**写出到本地文件，即/data/data/包名/files/
	 * @param context
	 * @param data 待写入字符数据
	 * @param fileName
	 * @param mode 私有文件：Context.MODE_PRIVATE<br/>
	 * 可读文件：Context.MODE_WORLD_READABLE<br/>
	 * 可写文件：Context.MODE_WORLD_WRITEABLE<br/>
	 * 可读可写文件：Context.MODE_WORLD_READABLE + Context.MODE_WORLD_WRITEABLE<br/>
	 */
	public static void Write2Local(Context context, String data, String fileName, int mode){
		try {
			// 写入到/data/data/包名/files/
			FileOutputStream fos = context.openFileOutput(fileName, mode);
			fos.write(data.getBytes());
			fos.flush();
			fos.close();
			Toast.makeText(context, "写入成功", Toast.LENGTH_SHORT).show();
		} catch (Exception e) {
			e.printStackTrace();
			Toast.makeText(context, "写入失败", Toast.LENGTH_SHORT).show();
		}
	}
	
	/**从本地文件（/data/data/包名/files/）读入
	 * @param context
	 * @param fileName
	 * @return 返回字符数据
	 */
	public static String ReadFromLocal(Context context, String fileName){
		StringBuilder data = new StringBuilder();
		String path = context.getFilesDir() + fileName;
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(path)));
			String line;
			while((line = reader.readLine()) != null){
				data.append(line); 
			}
			reader.close();
			Toast.makeText(context, "读取成功", Toast.LENGTH_SHORT).show();
		} catch (Exception e) {
			e.printStackTrace();
			Toast.makeText(context, "读取失败", Toast.LENGTH_SHORT).show();
		}
		return data.toString();
	}
	
	/**写出到SharedPreferences
	 * @param context
	 * @param data
	 * @param fileName
	 * @param mode 私有文件：Context.MODE_PRIVATE<br/>
	 * 可读文件：Context.MODE_WORLD_READABLE<br/>
	 * 可写文件：Context.MODE_WORLD_WRITEABLE<br/>
	 * 可读可写文件：Context.MODE_WORLD_READABLE + Context.MODE_WORLD_WRITEABLE<br/>
	 */
	public static void Write2SharedPrefs(Context context, String data, String fileName, int mode){
		//SharedPreferences的存储路径/data/data/包名/shared_prefs/
		SharedPreferences sp = context.getSharedPreferences(fileName, mode);
		//获取编辑对象
		Editor edit = sp.edit();
		//TODO 存储数据示例
		edit.putString("testKey", data);
		//提交
		edit.commit();
		Toast.makeText(context, "保存成功", Toast.LENGTH_SHORT).show();
	}

	/**从SharedPreferences读取数据
	 * @param context
	 * @param fileName
	 * @param mode
	 * @return 返回字符数据
	 */
	public static String ReadFromSharedPrefs(Context context, String fileName, int mode){
		//注意fileName要与写入的文件名相同
		SharedPreferences sp = context.getSharedPreferences(fileName, mode);
		//TODO 读取数据示例，注意key与写入时的key相同
		String data = sp.getString("testKey", null);
		if(!TextUtils.isEmpty(data)){
			return data;
		}else {
			return null;
		}
	}
}
