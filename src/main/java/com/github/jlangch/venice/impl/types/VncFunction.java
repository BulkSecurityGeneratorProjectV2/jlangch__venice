/*   __    __         _
 *   \ \  / /__ _ __ (_) ___ ___ 
 *    \ \/ / _ \ '_ \| |/ __/ _ \
 *     \  /  __/ | | | | (_|  __/
 *      \/ \___|_| |_|_|\___\___|
 *
 *
 * Copyright 2017-2020 Venice
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
package com.github.jlangch.venice.impl.types;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import com.github.jlangch.venice.impl.MetaUtil;
import com.github.jlangch.venice.impl.types.collections.VncHashMap;
import com.github.jlangch.venice.impl.types.collections.VncList;
import com.github.jlangch.venice.impl.types.collections.VncVector;
import com.github.jlangch.venice.impl.util.StringUtil;


public abstract class VncFunction 
	extends VncVal 
	implements IVncFunction, INamespaceAware {

	public VncFunction(final String name) {
		this(name, null, null);
	}
	
	public VncFunction(final String name, final VncVal meta) {
		this(name, null, meta);
	}

	public VncFunction(final String name, final VncVector params) {
		this(name, params, Constants.Nil);
	}

	public VncFunction(final String name, final VncVector params, final VncVal meta) {
		super(Constants.Nil);

		final int pos = name.indexOf("/");
		this.namespace = pos < 0 ? "core" : name.substring(0, pos);
		this.simpleName = pos < 0 ? name : name.substring(pos+1);
		this.qualifiedName = "core".equals(namespace) ? simpleName : namespace + "/" + simpleName;

		this.params = params;
		
		int fixedArgs = 0;
		boolean variadic = false;
		if (params != null) {
			for(VncVal p : params.getList()) {
				if (isElisionSymbol(p)) {
					variadic = true;
					break;
				}
				fixedArgs++;
			}
		}			
		this.fixedArgsCount = fixedArgs;
		this.variadicArgs = variadic;

		this.fnMeta.set(MetaUtil.setNamespace(meta, namespace));
		this.fnPrivate = MetaUtil.isPrivate(meta);
	}
	
	@Override
	public VncFunction withMeta(final VncVal meta) {
		this.fnMeta.set(meta);
		this.fnPrivate = MetaUtil.isPrivate(meta);
		return this;
	}
	
	@Override
	public VncKeyword getType() {
		return isMacro() ? TYPE_MACRO : TYPE_FUNCTION;
	}
	
	@Override
	public VncKeyword getSupertype() {
		return VncVal.TYPE;
	}

	@Override
	public List<VncKeyword> getAllSupertypes() {
		return Arrays.asList(VncVal.TYPE);
	}

	@Override
	public abstract VncVal apply(final VncList args);

	public VncVal applyOf(final VncVal... mvs) {
		return apply(VncList.of(mvs));
	}

	public boolean isRedefinable() { 
		return true; 
	}
	
	public VncVector getParams() { 
		return params; 
	}
	
	public boolean isMacro() { 
		return macro; 
	}
	
	public void setMacro() { 
		macro = true; 
	}
	
	@Override
	public String getSimpleName() { 
		return simpleName; 
	}
	
	@Override
	public String getQualifiedName() { 
		return qualifiedName; 
	}
	
	@Override
	public String getNamespace() {
		return namespace;
	}
	
	@Override
	public boolean hasNamespace() {
		return namespace != null;
	}

	public VncList getArgLists() { 
		return (VncList)getMetaVal(MetaUtil.ARGLIST, VncList.empty());
	}
	
	public VncVal getDoc() { 
		return getMetaVal(MetaUtil.DOC); 
	}
	
	public VncList getExamples() { 
		return (VncList)getMetaVal(MetaUtil.EXAMPLES, VncList.empty());
	}
	
	public int getFixedArgsCount() {
		return fixedArgsCount;
	}
	
	public boolean hasVariadicArgs() {
		return variadicArgs;
	}

	public VncVal getBody() { 
		return Constants.Nil;
	}

	@Override
	public VncVal getMeta() { 
		return fnMeta.get(); 
	}
	
	@Override
	public boolean isPrivate() {
		return fnPrivate;
	}
	
	@Override 
	public TypeRank typeRank() {
		return TypeRank.FUNCTION;
	}

	@Override
	public Object convertToJavaObject() {
		return null;
	}

	@Override 
	public String toString() {
		return String.format(
				"%s %s %s", 
				isMacro() ? "macro" : "function", 
				getQualifiedName(),
				new StringBuilder()
					.append("{")
					.append("visibility ")
					.append(isPrivate() ? ":private" : ":public")
					.append(", ns ")
					.append(StringUtil.quote(namespace == null ? "" : namespace, '\"'))
					.append("}"));
	}

	
	public static String createAnonymousFuncName() {
		return createAnonymousFuncName(null);
	}

	public static String createAnonymousFuncName(final String name) {
		return StringUtil.isEmpty(name)
				? "anonymous-" + UUID.randomUUID().toString()
				: "anonymous-" + name + "-" + UUID.randomUUID().toString();
	}

	private static boolean isElisionSymbol(final VncVal val) {
		return (val instanceof VncSymbol) && ((VncSymbol)val).getName().equals("&");
	}
	
	
	
	public static MetaBuilder meta() {
		return new MetaBuilder();
	}
	
	
	public static class MetaBuilder  {

		public MetaBuilder() {
		}
		
		public MetaBuilder arglists(final String... arglists) {
			meta.put(
				MetaUtil.ARGLIST, 
				VncList.ofList(Arrays.stream(arglists).map(s -> new VncString(s)).collect(Collectors.toList())));
			return this;
		}
		
		public MetaBuilder doc(final String doc) { 
			meta.put(MetaUtil.DOC, new VncString(doc));
			return this;
		}
		
		public MetaBuilder examples(final String... examples) { 
			meta.put(
				MetaUtil.EXAMPLES, 
				VncList.ofList(Arrays.stream(examples).map(s -> new VncString(s)).collect(Collectors.toList())));
			return this;
		}
			
		public VncHashMap build() {
			return new VncHashMap(meta);
		}

		private final HashMap<VncVal,VncVal> meta = new HashMap<>();
	}
	

    public static final VncKeyword TYPE_FUNCTION = new VncKeyword(":core/function");
    public static final VncKeyword TYPE_MACRO = new VncKeyword(":core/macro");

    private static final long serialVersionUID = -1848883965231344442L;

	private final VncVector params;
	private final String simpleName;
	private final String qualifiedName;
	private final String namespace;
	private final int fixedArgsCount;
	private final boolean variadicArgs;
	
	// Functions handle its meta data locally (functions cannot be copied)
	private final AtomicReference<VncVal> fnMeta = new AtomicReference<>(Constants.Nil);
	private volatile boolean fnPrivate;
	private volatile boolean macro = false;
}