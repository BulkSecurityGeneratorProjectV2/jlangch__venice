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

import java.net.InetAddress;
import java.util.Map;

import com.github.jlangch.venice.VncException;
import com.github.jlangch.venice.impl.types.VncBoolean;
import com.github.jlangch.venice.impl.types.VncFunction;
import com.github.jlangch.venice.impl.types.VncInteger;
import com.github.jlangch.venice.impl.types.VncJavaObject;
import com.github.jlangch.venice.impl.types.VncString;
import com.github.jlangch.venice.impl.types.VncVal;
import com.github.jlangch.venice.impl.types.collections.VncHashMap;
import com.github.jlangch.venice.impl.types.collections.VncList;
import com.github.jlangch.venice.impl.types.collections.VncVector;
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
						return VncBoolean.of(cidr.isInRange(((VncString)ip).getValue()));
					}
					else if (Types.isVncJavaObject(cidr_, CIDR.class)) {
						final CIDR cidr = (CIDR)((VncJavaObject)cidr_).getDelegate();
						return VncBoolean.of(cidr.isInRange(((VncString)ip).getValue()));				
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
						return VncBoolean.of(cidr.isInRange(inet));
					}
					else if (Types.isVncJavaObject(cidr_, CIDR.class)) {
						final InetAddress inet = (InetAddress)((VncJavaObject)ip).getDelegate();
						final CIDR cidr = (CIDR)((VncJavaObject)cidr_).getDelegate();
						return VncBoolean.of(cidr.isInRange(inet));
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

		

	///////////////////////////////////////////////////////////////////////////
	// InetAddress
	///////////////////////////////////////////////////////////////////////////

	public static VncFunction inet_addr = 
		new VncFunction(
				"cidr/inet-addr", 
				VncFunction
					.meta()
					.arglists("(cidr/inet-addr addr)")		
					.doc("Converts a stringified IPv4 or IPv6 to a Java InetAddress.")
					.examples(
						"(cidr/inet-addr \"222.192.0.0\")",
						"(cidr/inet-addr \"2001:0db8:85a3:08d3:1319:8a2e:0370:7347\")")
					.build()
		) {		
			public VncVal apply(final VncList args) {
				assertArity("cidr/inet-addr", args, 1);
	
				final String ip = Coerce.toVncString(args.first()).getValue();
				
				try {
					return new VncJavaObject(InetAddress.getByName(ip));
				}
				catch(Exception ex) {
					throw new VncException("Not an IP address: '" + ip + "'");
				}
			}
	
			private static final long serialVersionUID = -1848883965231344442L;
		};

	public static VncFunction inet_addr_to_bytes = 
		new VncFunction(
				"cidr/inet-addr-to-bytes", 
				VncFunction
					.meta()
					.arglists("(cidr/inet-addr-to-bytes addr)")		
					.doc("Converts a stringified IPv4 or IPv6 to an InetAddress byte vector.")
					.examples(
						"(cidr/inet-addr-to-bytes \"222.192.12.0\")",
						"(cidr/inet-addr-to-bytes \"2001:0db8:85a3:08d3:1319:8a2e:0370:7347\")")
					.build()
		) {		
			public VncVal apply(final VncList args) {
				assertArity("cidr/inet-addr-to-bytes", args, 1);
	
				final String ip = Coerce.toVncString(args.first()).getValue();
				
				try {
					final byte[] bytes = InetAddress.getByName(ip).getAddress();
					final VncInteger[] ints = new VncInteger[bytes.length];
					for(int ii=0; ii<bytes.length; ii++) {
						ints[ii] = new VncInteger(Byte.toUnsignedInt(bytes[ii]));
					}
					return VncVector.of(ints);
				}
				catch(Exception ex) {
					throw new VncException("Not an IP address: '" + ip + "'");
				}
			}
	
			private static final long serialVersionUID = -1848883965231344442L;
		};

	public static VncFunction inet_addr_from_bytes = 
		new VncFunction(
				"cidr/inet-addr-from-bytes", 
				VncFunction
					.meta()
					.arglists("(cidr/inet-addr-bytes addr)")		
					.doc(
						"Converts a IPv4 or IPv6 byte address (a vector of unsigned " +
						"integers) to a Java InetAddress.")
					.examples(
						"(cidr/inet-addr-from-bytes [222I 192I 12I 0I])",
						"(cidr/inet-addr-from-bytes [32I 1I 13I 184I 133I 163I 8I 211I 19I 25I 138I 46I 3I 112I 115I 71I])")
					.build()
		) {		
			public VncVal apply(final VncList args) {
				assertArity("cidr/inet-addr-from-bytes", args, 1);
	
				final VncList ints = Coerce.toVncList(args.first());
				
				try {
					final byte[] addr = new byte[ints.size()];
					for(int ii=0; ii<ints.size(); ii++) {
						addr[ii] = Coerce.toVncInteger(ints.nth(ii)).getValue().byteValue();
					}
					return new VncJavaObject(InetAddress.getByAddress(addr));
				}
				catch(Exception ex) {
					throw new VncException("Not an IP address: '" + args.first() + "'");
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
					.add(inet_addr_to_bytes)
					.add(inet_addr_from_bytes)
					.toMap();	
}
