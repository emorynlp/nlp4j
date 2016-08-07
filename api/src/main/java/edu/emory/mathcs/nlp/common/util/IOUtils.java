/**
 * Copyright 2015, Emory University
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
package edu.emory.mathcs.nlp.common.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tukaani.xz.LZMA2Options;
import org.tukaani.xz.XZInputStream;
import org.tukaani.xz.XZOutputStream;

import edu.emory.mathcs.nlp.common.constant.StringConst;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class IOUtils
{
	private static final Logger LOG = LoggerFactory.getLogger(IOUtils.class);
	private IOUtils() {}
	
	public static Object fromByteArray(byte[] array)
	{
		ByteArrayInputStream bin = new ByteArrayInputStream(array);
		Object obj = null;
		
		try
		{
			ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(bin));
			obj = in.readObject();
			in.close();
		}
		catch (Exception e) {e.printStackTrace();}
		
		return obj;
	}
	
	
	public static byte[] toByteArray(Object obj)
	{
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		
		try
		{
			ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(bos));
			out.writeObject(obj);
			out.close();
		}
		catch (IOException e) {e.printStackTrace();}
		
		return bos.toByteArray();
	}
	
	public static Map<String,byte[]> toByteMap(ZipInputStream stream) throws IOException
	{
		Map<String,byte[]> map = new HashMap<>();
		ZipEntry zEntry;
		
		while ((zEntry = stream.getNextEntry()) != null)
			map.put(zEntry.getName(), toByteArray(stream));

		stream.close();
		return map;
	}
	
	public static byte[] toByteArray(ZipInputStream in) throws IOException
	{
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int count;
		
		while ((count = in.read(buffer)) != -1)
			bout.write(buffer, 0, count);
         
		return bout.toByteArray();
	}
	
	/** @param in internally wrapped by {@code new BufferedReader(new InputStreamReader(in))}. */
	static public BufferedReader createBufferedReader(InputStream in)
	{
		return new BufferedReader(new InputStreamReader(in));
	}
	
	static public BufferedReader createBufferedReader(File file)
	{
		try
		{
			return new BufferedReader(new InputStreamReader(new FileInputStream(file)));
		}
		catch (FileNotFoundException e) {e.printStackTrace();}
		
		return null;
	}
	
	static public BufferedReader createBufferedReader(String filename)
	{
		return createBufferedReader(createFileInputStream(filename));
	}
	
	/** @param in internally wrapped by {@code new PrintStream(new BufferedOutputStream(out))}. */
	static public PrintStream createBufferedPrintStream(OutputStream out)
	{
		return new PrintStream(new BufferedOutputStream(out));
	}
	
	static public PrintStream createBufferedPrintStream(String filename)
	{
		return createBufferedPrintStream(createFileOutputStream(filename));
	}
	
	static public FileInputStream createFileInputStream(String filename)
	{
		FileInputStream in = null;
		
		try
		{
			in = new FileInputStream(filename);
		}
		catch (FileNotFoundException e) {e.printStackTrace();}
		
		return in;
	}
	
	static public FileInputStream[] createFileInputStreams(String[] filelist)
	{
		int i, len = filelist.length;
		FileInputStream[] in = new FileInputStream[len];
		
		for (i=0; i<len; i++)
			in[i] = IOUtils.createFileInputStream(filelist[i]);
		
		return in;
	}
	
	static public FileOutputStream createFileOutputStream(String filename)
	{
		FileOutputStream out = null;
		
		try
		{
			out = new FileOutputStream(filename);
		}
		catch (FileNotFoundException e) {e.printStackTrace();}
		
		return out;
	}
	
	static public XZOutputStream createXZBufferedOutputStream(String filename)
	{
		return createXZBufferedOutputStream(filename, LZMA2Options.PRESET_DEFAULT);
	}
	
	static public XZOutputStream createXZBufferedOutputStream(String filename, int preset)
	{
		XZOutputStream zout = null;
		
		try
		{
			zout = new XZOutputStream(new BufferedOutputStream(new FileOutputStream(filename)), new LZMA2Options(preset));
		}
		catch (IOException e) {e.printStackTrace();}
		
		return zout;
	}
	
	static public XZOutputStream createXZBufferedOutputStream(OutputStream out)
	{
		return createXZBufferedOutputStream(out, LZMA2Options.PRESET_DEFAULT);
	}
	
	static public XZOutputStream createXZBufferedOutputStream(OutputStream out, int preset)
	{
		XZOutputStream zout = null;
		
		try
		{
			zout = new XZOutputStream(new BufferedOutputStream(out), new LZMA2Options(preset));
		}
		catch (IOException e) {e.printStackTrace();}
		
		return zout;
	}
	
	static public XZInputStream createXZBufferedInputStream(InputStream in)
	{
		XZInputStream zin = null;
		
		try
		{
			zin = new XZInputStream(new BufferedInputStream(in));
		}
		catch (IOException e) {e.printStackTrace();}
		
		return zin;
	}
	
	static public XZInputStream createXZBufferedInputStream(String filename)
	{
		XZInputStream zin = null;
		
		try
		{
			zin = new XZInputStream(new BufferedInputStream(new FileInputStream(filename)));
		}
		catch (IOException e) {e.printStackTrace();}
		
		return zin;
	}
	
	static public ObjectInputStream createObjectXZBufferedInputStream(String filename)
	{
		ObjectInputStream oin = null;
		
		try
		{
			oin = new ObjectInputStream(createXZBufferedInputStream(filename));
		}
		catch (IOException e) {e.printStackTrace();}
		
		return oin;
	}
	
	static public ObjectInputStream createObjectXZBufferedInputStream(InputStream in)
	{
		ObjectInputStream oin = null;
		
		try
		{
			oin = new ObjectInputStream(createXZBufferedInputStream(in));
		}
		catch (IOException e) {e.printStackTrace();}
		
		return oin;
	}
	
	static public ObjectOutputStream createObjectXZBufferedOutputStream(String filename)
	{
		return createObjectXZBufferedOutputStream(filename, LZMA2Options.PRESET_DEFAULT);
	}
	
	static public ObjectOutputStream createObjectXZBufferedOutputStream(String filename, int preset)
	{
		ObjectOutputStream out = null;
		
		try
		{
			out = new ObjectOutputStream(createXZBufferedOutputStream(filename, preset));
		}
		catch (IOException e) {e.printStackTrace();}
		
		return out;
	}
	
	static public ObjectOutputStream createObjectXZBufferedOutputStream(OutputStream out)
	{
		return createObjectXZBufferedOutputStream(out, LZMA2Options.PRESET_DEFAULT);
	}
	
	static public ObjectOutputStream createObjectXZBufferedOutputStream(OutputStream out, int preset)
	{
		ObjectOutputStream oout = null;
		
		try
		{
			oout = new ObjectOutputStream(createXZBufferedOutputStream(out, preset));
		}
		catch (IOException e) {e.printStackTrace();}
		
		return oout;
	}
	
	/** @param in internally wrapped by {@code new ByteArrayInputStream(str.getBytes())}. */
	static public ByteArrayInputStream createByteArrayInputStream(String s)
	{
		return new ByteArrayInputStream(s.getBytes());
	}
	
	public static InputStream getInputStreamsFromResource(String path)
	{
		return IOUtils.class.getResourceAsStream(StringConst.FW_SLASH+path);
	}
	
	public static InputStream getInputStream(String path)
	{
		InputStream in = IOUtils.getInputStreamsFromResource(path);
		return (in != null) ? in : IOUtils.createFileInputStream(path);
	}

	/* An alternative set of APIs that allow for various formats and data in non-default file systems
	 * while still using the XML config files. */

	/**
	 * Open an input stream to a file.
	 * The file can be in the classpath, or named by a URI. The rules are as follows:
	 * <br>
	 * If the pathname contains a colon, we attempt to parse it into a URI and ask the URI for a
	 * {@link java.nio.file.Path}. If that fails, we treat the string as if it has no colon.
	 * In the no-colon case, we try for the classpath first (prepending a '/'),
	 * using the thread context classloader.
	 * If that fails, we look in the standard file system.
	 * @param pathname the name of the file.
	 * @return the stream.
	 * @throws IOException something went wrong.
	 */
	static public InputStream createArtifactInputStream(String pathname) throws IOException
	{
		Path path;
		InputStream baseStream = null;
		if (pathname.contains(":")) {
			try {
				path = Paths.get(new URI(pathname));
				baseStream = Files.newInputStream(path, StandardOpenOption.READ);
			} catch (URISyntaxException e) {
				LOG.debug("Failed to treat {} as a URI/path, falling back.", pathname);
				// leave
			}
		}

		if (baseStream == null) {
			baseStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(pathname);
			if (baseStream == null) {
				LOG.debug("{} not found in classpath, falling back to file system.", pathname);
				baseStream = Files.newInputStream(Paths.get(pathname));
			}
		}
		return wrapStream(baseStream, pathname);
	}

	/**
	 * Return a stream, set up to decompress as needed, for a resource found via classloading.
	 * @param classpath the pathname of the resource.
	 * @param classLoader the classloader to search. If {@code null}, this method uses
	 *                    the thread context class loader.
	 * @return the stream.
	 * @throws IOException if something goes wrong.
	 */
	static public InputStream createArtifactInputStreamForClasspath(String classpath, ClassLoader classLoader) throws IOException {
		if (classLoader == null) {
			classLoader = Thread.currentThread().getContextClassLoader();
		}
		InputStream baseStream = classLoader.getResourceAsStream(classpath);
		if (baseStream == null) {
			throw new IOException(String.format("Could not find %s in provided class loader.",
					classpath));
		}
		return wrapStream(baseStream, classpath);
	}

	static public InputStream createArtifactInputStream(Path path) throws IOException
	{
		String name = path.getFileName().toString();
		InputStream baseStream = Files.newInputStream(path, StandardOpenOption.READ);
		return wrapStream(baseStream, name);
	}

	private static InputStream wrapStream(InputStream baseStream, String filename) throws IOException
	{
		baseStream = new BufferedInputStream(baseStream);
		if (filename.endsWith(".xz")) {
			baseStream = new XZInputStream(baseStream);
		} else if (filename.endsWith(".gz")) {
			baseStream = new GZIPInputStream(baseStream);
		}
		return baseStream;
	}

	/**
	 * Open an input stream that reads a file in Serialized java format.
	 * @param pathname the pathname, which can be a classpath, or a URI.
	 * @return the stream.
	 * @throws IOException if something goes wrong.
	 * @see #createArtifactInputStream(String)
	 */
	static public ObjectInputStream createArtifactObjectInputStream(String pathname) throws IOException
	{
		return new ObjectInputStream(createArtifactInputStream(pathname));
	}

	/**
	 * Open an input stream that reads a {@link Path} in Serialized java format.
	 * @param pathname the pathname.
	 * @return the stream.
	 * @throws IOException if something goes wrong.
	 * @see #createArtifactInputStream(String)
	 */
	static public ObjectInputStream createArtifactObjectInputStream(Path pathname) throws IOException
	{
		return new ObjectInputStream(createArtifactInputStream(pathname));
	}
}
