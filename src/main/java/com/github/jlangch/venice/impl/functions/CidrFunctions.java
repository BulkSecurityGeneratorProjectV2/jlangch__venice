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
package com.github.jlangch.venice.impl.functions;

import static com.github.jlangch.venice.impl.functions.FunctionsUtil.assertArity;
import static com.github.jlangch.venice.impl.types.Constants.False;
import static com.github.jlangch.venice.impl.types.Constants.True;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.util.Map;

import com.github.jlangch.venice.VncException;
import com.github.jlangch.venice.impl.types.VncFunction;
import com.github.jlangch.venice.impl.types.VncJavaObject;
import com.github.jlangch.venice.impl.types.VncString;
import com.github.jlangch.venice.impl.types.VncVal;
import com.github.jlangch.venice.impl.types.collections.VncHashMap;
import com.github.jlangch.venice.impl.types.collections.VncList;
import com.github.jlangch.venice.impl.types.util.Coerce;
import com.github.jlangch.venice.impl.types.util.Types;
import com.github.jlangch.venice.impl.util.CIDR;


public class CidrFunctions {

	///////////////////////////////////////////////////////////////////////////
	// CIDR
	///////////////////////////////////////////////////////////////////////////

	public static VncFunction parse = 
		new VncFunction(
				"cidr/parse", 
				VncFunction
					.meta()
					.arglists("(cidr/parse cidr)")		
					.doc("Parses CIDR IP blocks to an IP address range. Supports both IPv4 and IPv6.")
					.examples(
						"(cidr/parse \"222.192.0.0/11\")",
						"(cidr/parse \"2001:0db8:85a3:08d3:1319:8a2e:0370:7347/64\")")
					.build()
		) {		
			public VncVal apply(final VncList args) {
				assertArity("cidr/parse", args, 1);
	
				final VncVal cidr = args.first();
				if (Types.isVncJavaObject(cidr, CIDR.class)) {
					return cidr;
				}
				else {
					return new VncJavaObject(CIDR.parse(Coerce.toVncString(cidr).getValue()));
				}
			}
	
			private static final long serialVersionUID = -1848883965231344442L;
		};

	public static VncFunction in_range_Q = 
		new VncFunction(
				"cidr/in-range?", 
				VncFunction
					.meta()
					.arglists("(cidr/in-range? ip cidr)")		
					.doc(
						"Returns true if the ip adress is within the ip range of the cidr else false. " + 
						"ip may be a string or a :java.net.InetAddress, cidr may be a string " + 
						"or a CIDR Java object obtained from 'cidr/parse'.")
					.examples(
						"(cidr/in-range? \"222.220.0.0\" \"222.220.0.0/11\")",
						"(cidr/in-range? (cidr/inet-addr \"222.220.0.0\") \"222.220.0.0/11\")",
						"(cidr/in-range? \"222.220.0.0\" (cidr/parse \"222.220.0.0/11\"))")
					.build()
		) {		
			public VncVal apply(final VncList args) {
				assertArity("cidr/in-range?", args, 2);
	
				final VncVal ip = args.first();
				final VncVal cidr_ = args.second();
				
				
				if (Types.isVncString(ip)) {
					if (Types.isVncString(cidr_)) {
						final CIDR cidr = CIDR.parse(((VncString)cidr_).getValue());
						return cidr.isInRange(((VncString)ip).getValue()) ? True : False;
					}
					else if (Types.isVncJavaObject(cidr_, CIDR.class)) {
						final CIDR cidr = (CIDR)((VncJavaObject)cidr_).getDelegate();
						return cidr.isInRange(((VncString)ip).getValue()) ? True : False;				
					}
					else {
						throw new VncException(String.format(
								"Invalid argument type %s while calling function 'cidr/in-range?'",
								Types.getType(cidr_)));
					}					
				}
				else if (Types.isVncJavaObject(ip, InetAddress.class)) {				
					if (Types.isVncString(cidr_)) {
						final InetAddress inet = (InetAddress)((VncJavaObject)ip).getDelegate();
						final CIDR cidr = CIDR.parse(((VncString)cidr_).getValue());
						return cidr.isInRange(inet) ? True : False;
					}
					else if (Types.isVncJavaObject(cidr_, CIDR.class)) {
						final InetAddress inet = (InetAddress)((VncJavaObject)ip).getDelegate();
						final CIDR cidr = (CIDR)((VncJavaObject)cidr_).getDelegate();
						return cidr.isInRange(inet) ? True : False;
					}
					else {
						throw new VncException(String.format(
								"Invalid argument type %s while calling function 'cidr/in-range?'",
								Types.getType(cidr_)));
					}					
				}
				else {
					throw new VncException(String.format(
							"Invalid argument type %s while calling function 'cidr/in-range?'",
							Types.getType(ip)));
				}
			}
	
			private static final long serialVersionUID = -1848883965231344442L;
		};

	public static VncFunction inet_addr = 
		new VncFunction(
				"cidr/inet-addr", 
				VncFunction
					.meta()
					.arglists("(cidr/inet-addr addr)")		
					.doc("Converts an stringified IPv4 or IPv6 to a Java InetAddress.")
					.examples(
						"(cidr/inet-addr \"222.192.0.0\")",
						"(cidr/inet-addr \"2001:0db8:85a3:08d3:1319:8a2e:0370:7347\")")
					.build()
		) {		
			public VncVal apply(final VncList args) {
				assertArity("cidr/inet-addr", args, 1);
	
				final String ip = Coerce.toVncString(args.first()).getValue();
				
				try {
					return ip.contains(".")
							? new VncJavaObject(Inet4Address.getByName(ip))
							: new VncJavaObject(Inet6Address.getByName(ip));
				}
				catch(Exception ex) {
					throw new VncException("Not an IP address: '" + ip + "'");
				}
			}
	
			private static final long serialVersionUID = -1848883965231344442L;
		};
		
	///////////////////////////////////////////////////////////////////////////
	// types_ns is namespace of type functions
	///////////////////////////////////////////////////////////////////////////

	public static Map<VncVal, VncVal> ns = 
			new VncHashMap
					.Builder()
					.add(parse)
					.add(in_range_Q)
					.add(inet_addr)
					.toMap();	
}
