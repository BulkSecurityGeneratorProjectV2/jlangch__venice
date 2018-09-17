/*   __    __         _
 *   \ \  / /__ _ __ (_) ___ ___ 
 *    \ \/ / _ \ '_ \| |/ __/ _ \
 *     \  /  __/ | | | | (_|  __/
 *      \/ \___|_| |_|_|\___\___|
 *
 *
 * Copyright 2017-2018 Venice
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.jlangch.venice.impl.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Date;

import com.github.jlangch.venice.FileException;


/**
 * File utilities
 */
public class FileUtil {

	private FileUtil() {
	}

	/**
	 * Copies a file.
	 * 
	 * @param source The source file
	 * @param destination The destination file
	 * @param overwrite if true overwrite the destination file
	 * @param progressListener an optional progress listener
	 */
	public static void copy(
			final File source, 
			final File destination, 
			final boolean overwrite
	) {
		FileInputStream inStream = null;
		FileOutputStream outStream = null;
		
		try {
			if (overwrite && destination.exists()) {
				if (!destination.delete()) {
					throw new RuntimeException(
							"Failed to delete existing destination file: " 
								+ destination);
				}
			}

			inStream = new FileInputStream(source);
			outStream = new FileOutputStream(destination);

			IOStreamUtil.copy(inStream, outStream);
			
			// Closing an output stream is critical especially with file I/O
			// on network file systems => catch exceptions
			outStream.close();
			outStream = null;
		} 
		catch (IOException ex) {
			throw new FileException(
					"Failed to copy file from <" + source + "> to <" + destination + ">.", 
					ex);
		}
		catch (RuntimeException ex) {
			throw new FileException(
					"Failed to copy file from <" + source + "> to <" + destination + ">.", 
					ex);
		} 
		finally {
			silentClose(inStream);
			silentClose(outStream);
		}
	}

	/**
	 * Loads binary data from a file.
	 * 
	 * @param source The source file
	 * @return the loaded file data
	 */
	public static byte[] load(final File source) {
		InputStream inStream = null;
		ByteArrayOutputStream outStream = null;
		
		try {
			inStream = new FileInputStream(source);
			outStream = new ByteArrayOutputStream();

			IOStreamUtil.copy(inStream, outStream);
			
			outStream.flush();	
			return outStream.toByteArray();
		} 
		catch (IOException ex) {
			throw new FileException("Failed to load file <" + source + ">.", ex);
		}
		catch (RuntimeException ex) {
			throw new FileException("Failed to load file <" + source + ">.", ex);
		}
		finally {
			silentClose(inStream);
			silentClose(outStream);
		}
	}
	
	/**
	 * Save to file.
	 * 
	 * @param data the data to save to the file
	 * @param destination The destination file
	 * @param overwrite if true overwrite the destination file
	 */
	public static void save(
			final byte[] data, 
			final File destination, 
			final boolean overwrite
	) {
		OutputStream outStream = null;
		
		try {
			if (overwrite && destination.exists()) {
				if (!destination.delete()) {
					throw new RuntimeException(
							"Failed to delete existing destination file: " 
								+ destination);
				}
			}

			outStream = new FileOutputStream(destination);

			outStream.write(data, 0, data.length);
						
			outStream.flush();
			
			// Closing an output stream is critical especially with file I/O
			// on network file systems => catch exceptions
			outStream.close();
			outStream = null;
		} 
		catch (IOException ex) {
			throw new FileException("Failed to save to file <" + destination + ">.", ex);
		}
		catch (RuntimeException ex) {
			throw new FileException("Failed to save to file <" + destination + ">.", ex);
		}
		finally {
			silentClose(outStream);
		}
	}

	/**
	 * Append to file.
	 * 
	 * @param data the data to append to the file
	 * @param destination The destination file
	 */
	public static void append(final byte[] data, final File destination) {
		OutputStream outStream = null;
		
		try {
			outStream = new FileOutputStream(destination, true);

			outStream.write(data, 0, data.length);
						
			outStream.flush();
			
			// Closing an output stream is critical especially with file I/O
			// on network file systems => catch exceptions
			outStream.close();
			outStream = null;
		} 
		catch (Exception ex) {
			throw new FileException(
					"Failed to append to file <" + destination + ">.", 
					ex);
		}
		finally {
			silentClose(outStream);
		}
	}

	
	/**
	 * Save to file.
	 * 
	 * @param inStream the input stream the data is read from to save to the file.
	 *        <b>Note:</b> The caller is responsible to close the in stream
	 * @param destination The destination file
	 * @param overwrite if true overwrite the destination file
	 */
	public static void save(
			final InputStream inStream, 
			final File destination, 
			final boolean overwrite
	) {
		OutputStream outStream = null;
		
		try {
			if (overwrite && destination.exists()) {
				if (!destination.delete()) {
					throw new RuntimeException(
							"Failed to delete existing destination file: " 
								+ destination);
				}
			}

			outStream = new FileOutputStream(destination);

			IOStreamUtil.copy(inStream, outStream);
						
			outStream.flush();
			
			// Closing an output stream is critical especially with file I/O
			// on network file systems
			outStream.close();
			outStream = null;
		} 
		catch (IOException ex) {
			throw new FileException("Failed to data to file <" + destination + ">.", ex);
		}
		catch (RuntimeException ex) {
			throw new FileException("Failed to data to file <" + destination + ">.", ex);
		}
		finally {
			silentClose(outStream);
		}
	}
	
	/**
	 * Save to file.
	 * 
	 * @param data the data to save to the file
	 * @param destination The destination file
	 * @param overwrite if true overwrite the destination file
	 */
	public static void save(
			final String text, 
			final File destination, 
			final boolean overwrite
	) {
		try {
			save(text.getBytes("utf-8"), destination, overwrite);
		} 
		catch (UnsupportedEncodingException ex) {
			throw new FileException("Failed to save to file <" + destination + ">.", ex);
		}
	}

	/**
	 * Deletes files from a given directory.
	 * 
	 * @param path A directory from which files are processed
	 * @param filter A filter that denotes the files to be deleted
	 * @return the number of deleted files
	 */
	public static int deleteFiles(final File path, final FilenameFilter filter) {
		if (path == null) {
			throw new IllegalArgumentException("A path must not be null");
		}
		if (filter == null) {
			throw new IllegalArgumentException("A filter must not be null");
		}
		
		int count = 0;
		File[] files = path.listFiles(filter);

		if (files != null) {
			for (int ii=0; ii<files.length; ii++) {
				if (files[ii].delete()) {
					count++;
				}
			}
		}
		
		return count;
	}

	/**
	 * @return The default temp directory
	 */
	public static File getTempDir() {
		return new File(System.getProperty("java.io.tmpdir"));
	}

	/**
	 * @return The user's directory
	 */
	public static File getUserDir() {
		return new File(System.getProperty("user.dir"));
	}

	/**
	 * @return The current working directory (absolute path)
	 */
	public static String getCurrentWorkDir() {
		File f = new java.io.File(".");
		String cwd = f.getAbsolutePath();
		return cwd.substring(0, cwd.length() - 2);
	}

	/**
	 * @param path a path
	 * @return true if the path is an absolute file path
	 */
	public static boolean isAbsoultePath(final String path) {
		if (path == null) {
			throw new IllegalArgumentException("A 'path' must not be <null>");
		}
		
		if (path.startsWith("/") || path.startsWith("\\")) {
			return true;
		}
		else {
			if (path.matches("^[A-Z]:.*")) {
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Touches a file. Creates the file if does not exist.
	 * 
	 * @param file A file (The parent directory must exist)
	 */
	public static void touch(final File file) {
		if (file == null) {
			throw new IllegalArgumentException("A file must not be null");
		}

		touch(file, System.currentTimeMillis());
	}

	/**
	 * Touches a file. Creates the file if does not exist.
	 * 
	 * @param file A file (The parent directory must exist)
	 * @param modificationTime A modification time
	 */
	public static void touch(final File file, final long modificationTime) {
		if (file == null) {
			throw new IllegalArgumentException("A file must not be null");
		}

		if (!file.exists()) {
			try {
				if (!file.createNewFile()) {
					throw new FileException("File <" + file + "> existed already");
				}
			} 
			catch (IOException ex) {
				throw new FileException("Failed to create new file <" + file + ">.", ex);
			}
		}

		if (!file.canWrite()) {
			throw new FileException(
					"Can not change modification date of read-only-file <" + file + ">.");
		}
			
		if (!file.setLastModified(modificationTime)) {
			throw new FileException(
					"Can not change modification date of file <" + file + ">.");
		}
	}

	/**
	 * Deletes a file or an empty directory.
	 * 
	 * @param file A file
	 * @throws FileException if the file could not be deleted
	 */
	public static void delete(final File file) {
		if (file == null) {
			throw new IllegalArgumentException("A file must not be null");
		}
		
		if (file.exists()) {
			boolean deleted = file.delete();
			if (!deleted) {
				throw new FileException(
						"Failed to delete " 
							+ (file.isDirectory() ? "directory" : "file")
							+ " <" + file + ">.");			
			}
		}
	}

	/**
	 * Creates a directory.
	 * 
	 * @param dir A directory
	 * @throws FileException if the directory could not be created
	 */
	public static void mkdir(final File dir) {
		if (dir == null) {
			throw new IllegalArgumentException("A dir must not be null");
		}
		
		if (!dir.isDirectory()) {
			boolean created = dir.mkdir();
			if (!created) {
				throw new FileException("Failed to create directory <" + dir + ">.");			
			}
		}
	}

	/**
	 * Creates a directory including any necessary but nonexistent parent directories.
	 * 
	 * @param dir A directory
	 * @throws FileException if the directory could not be created
	 */
	public static void mkdirs(final File dir) {
		if (dir == null) {
			throw new IllegalArgumentException("A dir must not be null");
		}
		
		if (!dir.isDirectory()) {
			boolean created = dir.mkdirs();
			if (!created) {
				throw new FileException(
						"Failed to create directory <" + dir + "> with its nonexistent parent directories.");			
			}
		}
	}

	/**
	 * Removes a directory recursively
	 * 
	 * @param dir A directory
	 * @throws FileException if the directory could not be removed
	 */
	public static void rmdir(final File dir) {
		if (dir == null) {
			throw new IllegalArgumentException("A dir must not be null");
		}

		try {
			doRmDir(dir, 0);
		}
		catch(Exception ex) {
			throw new FileException(
					"Failed to delete directory <" + dir + ">.", ex);			
		}
	}


	/**
	 * Copies a directory recursively
	 * 
	 * @param srcdir A source directory
	 * @param dstdir A destination directory
	 * @throws FileException if the directory could not be copied
	 */
	public static void copydir(final File srcdir, final File dstdir) {
		if (srcdir == null) {
			throw new IllegalArgumentException("A source dir must not be null");
		}
		if (dstdir == null) {
			throw new IllegalArgumentException("A destination dir must not be null");
		}

		try {
			doCopyDir(srcdir, dstdir, 0);
		}
		catch(RuntimeException ex) {
			throw new FileException(
					"Failed to copy directory <" + srcdir + ">.", ex);			
		}
	}

	/**
	 * Get the file creation date
	 * 
	 * @param file A file
	 * @param fileExt the new file extension
	 * @return The creation date
	 */
    public static Date getFileCreationDate(final File file) {
    	try {
	    	final BasicFileAttributes attr = Files.readAttributes(
	    										file.toPath(),
	    										BasicFileAttributes.class);
	    	
	    	return new Date(attr.creationTime().toMillis());
    	}
    	catch(Exception ex) {
			throw new FileException("Failed to get file creation date. File " + file.getPath(), ex);			
    	}
    }

	/**
	 * Replace a file extension. If the file name has no file extension it will be added.
	 * 
	 * @param fileName A file name
	 * @param fileExt the new file extension
	 * @return The file name enhanced by an index
	 */
	public static String replaceFileExt(final String fileName, final String fileExt) {
		if (fileName == null) {
			throw new IllegalArgumentException("A fileName must not be null");
		}
		if (fileExt == null) {
			throw new IllegalArgumentException("A fileExt must not be null");
		}

		String extension = fileExt;
		if (extension.startsWith(".")) {
			extension = extension.substring(1);
		}
		int pos = fileName.lastIndexOf('.');
		return (pos < 0 ? fileName : fileName.substring(0, pos)) + "." + extension;
	}
	
	/**
	 * Returns the file's extension
	 *
	 * @param fileName a file name
	 * @return The file's extension or null if there is none
	 */
	public static String getFileExt(final String fileName) {
		if (fileName == null) {
			throw new IllegalArgumentException("A fileName must not be null");
		}

		int pos = fileName.lastIndexOf('.');
		return (pos < 0) ? null : fileName.substring(pos+1);
	}

	public static String getFileBaseName(final String fileName) {
		int pos = fileName.lastIndexOf('.');
		return (pos < 0) ? null : fileName.substring(0, pos);
	}

	public static String getFileExtensionLowerCase(final String fileName) {	
		String ext = getFileExt(fileName);
		return ext == null ? null : ext.toLowerCase();
	}


	private static void doRmDir(final File dir, final int level) {
		if (level > MAX_DIR_LEVELS) {
			throw new FileException("Reached max dir level (" + MAX_DIR_LEVELS + ")");		
		}
		
		for (File file : dir.listFiles()) {
        	if (file.isDirectory()) {
        		doRmDir(file, level + 1);
        	}
        	else {
        		if (!file.delete()) {
					throw new RuntimeException(
							"Could not delete file " + file.getAbsolutePath());        			
        		}
        	}
        }

		if (!dir.delete()) {
			throw new RuntimeException(
					"Could not delete directory " + dir.getAbsolutePath());        			
		}
	}

	private static void doCopyDir(final File srcdir, final File dstdir, final int level) {
		if (level > MAX_DIR_LEVELS) {
			throw new FileException("Reached max dir level (" + MAX_DIR_LEVELS + ")");		
		}

		if (!dstdir.exists()) {
			mkdir(dstdir);
		}
		
		for (File file : srcdir.listFiles()) {
        	if (file.isDirectory()) {
        		doCopyDir(
        				new File(srcdir, file.getName()), 
        				new File(dstdir, file.getName()), level + 1);
        	}
        	else {
        		copy(file, new File(dstdir, file.getName()), true);
        	}
        }
	}


	private static void silentClose(final InputStream is) {
		if (is != null) {
			try { is.close(); } catch(Exception ex) { /* ignore */ }
		}
	}

	private static void silentClose(final OutputStream os) {
		if (os != null) {
			try { os.close(); } catch(Exception ex) { /* ignore */ }
		}
	}
	

	private static final int MAX_DIR_LEVELS = 32;
}
