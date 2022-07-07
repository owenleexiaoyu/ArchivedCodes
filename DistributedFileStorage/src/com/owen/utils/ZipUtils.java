package com.owen.utils;
import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * 閫氳繃Java鐨刏ip杈撳叆杈撳嚭娴佸疄鐜板帇缂╁拰瑙ｅ帇鏂囦欢
 */
public final class ZipUtils {
    /**
     * 鍘嬬缉鏂囦欢
     */
    public static void zip(String filePath,String zipName) {
        File target = new File(zipName);
        File source = new File(filePath);
        if (source.exists()) {
        	try{
        		if(!target.exists()){
        			target.createNewFile();
        		}
        		if (target.exists()) {
        			target.delete(); 
        			target.createNewFile();
        		}
        	}catch (IOException e) {
				e.printStackTrace();
			}
            
            FileOutputStream fos = null;
            ZipOutputStream zos = null;
            try {
                fos = new FileOutputStream(target);
                zos = new ZipOutputStream(new BufferedOutputStream(fos));
                // 娣诲姞瀵瑰簲鐨勬枃浠禘ntry
                addEntry("/", source, zos);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } finally {
                closeQuietly(zos, fos);
            }
        }
    }

    /**
     * 鎵弿娣诲姞鏂囦欢Entry
     *
     * @param base
     *            鍩鸿矾寰�
     *
     * @param source
     *            婧愭枃浠�
     * @param zos
     *            Zip鏂囦欢杈撳嚭娴�
     * @throws IOException
     */
    private static void addEntry(String base, File source, ZipOutputStream zos)
            throws IOException {
        // 鎸夌洰褰曞垎绾э紝褰㈠锛�/aaa/bbb.txt
        String entry = base + source.getName();
        if (source.isDirectory()) {
            for (File file : source.listFiles()) {
                // 閫掑綊鍒楀嚭鐩綍涓嬬殑鎵�鏈夋枃浠讹紝娣诲姞鏂囦欢Entry
                addEntry(entry + "/", file, zos);
            }
        } else {
            FileInputStream fis = null;
            BufferedInputStream bis = null;
            try {
                byte[] buffer = new byte[1024 * 10];
                fis = new FileInputStream(source);
                bis = new BufferedInputStream(fis, buffer.length);
                int read = 0;
                zos.putNextEntry(new ZipEntry(entry));
                while ((read = bis.read(buffer, 0, buffer.length)) != -1) {
                    zos.write(buffer, 0, read);
                }
                zos.closeEntry();
            } finally {
                closeQuietly(bis, fis);
            }
        }
    }

    /**
     * 瑙ｅ帇鏂囦欢
     */
    public static void unzip(String filePath,String targetname) {
        File source = new File(filePath);
        if (source.exists()) {
            ZipInputStream zis = null;
            BufferedOutputStream bos = null;
            try {
                zis = new ZipInputStream(new FileInputStream(source));
                ZipEntry entry = null;
                while ((entry = zis.getNextEntry()) != null
                        && !entry.isDirectory()) {
                    File target = new File(targetname);
                    if(!target.exists()){
                    	target.createNewFile();
                    }
                    bos = new BufferedOutputStream(new FileOutputStream(target));
                    int read = 0;
                    byte[] buffer = new byte[1024 * 10];
                    while ((read = zis.read(buffer, 0, buffer.length)) != -1) {
                        bos.write(buffer, 0, read);
                    }
                    bos.flush();
                }
                zis.closeEntry();
            } catch (IOException e) {
                
            } finally {
                closeQuietly(zis, bos);
            }
        }
    }
    /**
     * 鍏抽棴涓�涓垨澶氫釜娴佸璞�
     *
     * @param closeables
     *            鍙叧闂殑娴佸璞″垪琛�
     * @throws IOException
     */
    public static void close(Closeable... closeables) throws IOException {
        if (closeables != null) {
            for (Closeable closeable : closeables) {
                if (closeable != null) {
                    closeable.close();
                }
            }
        }
    }

    /**
     * 鍏抽棴涓�涓垨澶氫釜娴佸璞�
     *
     * @param closeables
     *            鍙叧闂殑娴佸璞″垪琛�
     */
    public static void closeQuietly(Closeable... closeables) {
        try {
            close(closeables);
        } catch (IOException e) {
            // do nothing
        }
    }
    public static void main(String[] args) {
        ZipUtils.zip("D:\\test\\a.txt","D:\\test\\a.zip");

        ZipUtils.unzip("D:\\test\\a.txt.zip","D:\\test\\a.txt");
    }
}