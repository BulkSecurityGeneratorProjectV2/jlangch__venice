package com.github.jlangch.venice.impl.docgen.util;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.xhtmlrenderer.pdf.ITextOutputDevice;
import org.xhtmlrenderer.pdf.ITextUserAgent;

import com.github.jlangch.venice.impl.util.ClassPathResource;
import com.github.jlangch.venice.impl.util.StringUtil;


/**
 * An XhtmlRenderer User Agent that loads resources from the classpath
 * 
 * <pre>
 *   ITextRenderer renderer = new ITextRenderer(..);
 *   ITextUserAgent userAgent = new ClasspathUserAgent(
 *                                      renderer.getOutputDevice());
 *   userAgent.setSharedContext(renderer.getSharedContext());
 *   renderer.getSharedContext().setUserAgentCallback(userAgent);
 *   renderer.setDocument(doc, "classpath://templates/pdf/");
 * </pre>
 * 
 * <p>With alternate base paths
 * <pre>
 *   ITextRenderer renderer = new ITextRenderer(..);
 *   ITextUserAgent userAgent = new ClasspathUserAgent(renderer.getOutputDevice()) 
 *                                      .addAlternateBasePath("templates/pdf")
 *                                      .addAlternateBasePath("fonts");
 *   userAgent.setSharedContext(renderer.getSharedContext());
 *   renderer.getSharedContext().setUserAgentCallback(userAgent);
 *   renderer.setDocument(doc, "classpath:///");
 * </pre>
 * 
 * @author juerg
 */
public class ClasspathUserAgent extends ITextUserAgent {

	public ClasspathUserAgent(
		final ITextOutputDevice outputDevice
	) {
		super(outputDevice);
	}

	public ClasspathUserAgent addAlternateBasePath(final String path) {
		if (StringUtil.isBlank(path)) {
			throw new IllegalArgumentException("A path must not be blank");
		}

		final String altPath = stripLeadingTrailingSlash(path);
		if (StringUtil.isNotBlank(altPath)) {
			alternateBasePaths.add(altPath);
		}
		
		return this;
	}

	public ClasspathUserAgent addResource(final String name, final byte[] data) {
		if (StringUtil.isBlank(name)) {
			throw new IllegalArgumentException("A 'resource' name must not be blank");
		}
		if (data == null) {
			throw new IllegalArgumentException("A 'resource' data must not be null");
		}
		
		cachedResources.put(name, ByteBuffer.wrap(data));
		
		return this;
	}

	@Override
	protected InputStream resolveAndOpenStream(final String uri) {
		if (uri == null) return null;
		
		if (isClasspathScheme(uri)) {
			final String cpResource = stripClasspathScheme(uri);
			
			// [1] try to get the resource from the cached resources
			ByteBuffer data = cachedResources.get(cpResource);
			if (data != null) {
				return new ByteArrayInputStream(data.array());
			}
			
			// [2] try to get the resource from the classpath (root)
			data = slurp(new ClassPathResource(cpResource));
			if (data != null) {
				cachedResources.put(cpResource, data);
				return new ByteArrayInputStream(data.array());
			}

			// [3] try to get the resource from the alternate classpaths
			for(String path : alternateBasePaths) {
				data = slurp(new ClassPathResource(path + "/" + cpResource));
				if (data != null) {
					cachedResources.put(cpResource, data);
					return new ByteArrayInputStream(data.array());
				}
			}
			
			// [4] the resource has not been found
			return null;
		}
		else {
			return super.resolveAndOpenStream(uri);
		}
    }
	
	
	private boolean isClasspathScheme(final String uri) {
		return uri.startsWith("classpath:");
	}
	
	private String stripClasspathScheme(final String uri) {
		return uri.replaceFirst("^classpath:/*", "");
	}
	
	private String stripLeadingTrailingSlash(final String path) {
		if (StringUtil.isBlank(path)) {
			return path;
		}
		else {
			String p = path.trim();
			p = StringUtil.removeStart(p, "/");
			p = StringUtil.removeEnd(p, "/");
			return p;
		}
	}
	
	private ByteBuffer slurp(final ClassPathResource cpResource) {
		return cpResource.getResourceAsByteBuffer();
	}
	
	
	private final ConcurrentLinkedQueue<String> alternateBasePaths = new ConcurrentLinkedQueue<>();
	private final Map<String, ByteBuffer> cachedResources = new ConcurrentHashMap<>();
}
