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
package com.github.jlangch.venice.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


public class CommandLineArgs {

	public CommandLineArgs(final String[] args){
		this.args = args;
		
		parse(args);
	}
	
	public void parse(final String[] arguments){
		switchIndexes.clear();
		takenIndexes.clear();
		for(int ii=0; ii < args.length; ii++) {
			if(args[ii].startsWith("-") ){
				switchIndexes.put(args[ii], ii);
				takenIndexes.add(ii);
			}
		}
	}
	
	public String[] args() {
		return args;
	}
	
	public String arg(final int index){
		return args[index];
	}
	
	public boolean switchPresent(final String switchName) {
		return switchIndexes.containsKey(switchName);
	}
	
	public String switchValue(final String switchName) {
		return switchValue(switchName, null);
	}
	
	public String switchValue(final String switchName, final String defaultValue) {
		if (!switchIndexes.containsKey(switchName)) {
			return defaultValue;
		}
		
		final int switchIndex = switchIndexes.get(switchName);
		if (switchIndex + 1 < args.length) {
			takenIndexes.add(switchIndex +1);
			return args[switchIndex +1];
		}
		return defaultValue;
	}
	
	public Long switchLongValue(final String switchName) {
		return switchLongValue(switchName, null);
	}
	
	public Long switchLongValue(final String switchName, final Long defaultValue) {
		final String switchValue = switchValue(switchName, null);
		
		return switchValue == null ? defaultValue : Long.parseLong(switchValue);
	}
	
	public Double switchDoubleValue(final String switchName) {
		return switchDoubleValue(switchName, null);
	}
	
	public Double switchDoubleValue(final String switchName, final Double defaultValue) {
		String switchValue = switchValue(switchName, null);
		
		return switchValue == null ? defaultValue : Double.parseDouble(switchValue);
	}
	
	
	public String[] switchValues(final String switchName) {
		if(!switchIndexes.containsKey(switchName)) return new String[0];
		
		final int switchIndex = switchIndexes.get(switchName);
		
		int nextArgIndex = switchIndex + 1;
		while(nextArgIndex < args.length && !args[nextArgIndex].startsWith("-")){
			takenIndexes.add(nextArgIndex);
			nextArgIndex++;
		}
	
		final String[] values = new String[nextArgIndex - switchIndex - 1];
		for(int jj=0; jj < values.length; jj++){
		    values[jj] = args[switchIndex + jj + 1];
		}
		return values;
	}
	
	public String[] targets() {
		final String[] targetArray = new String[args.length - takenIndexes.size()];
		int targetIndex = 0;
		for(int ii=0; ii < args.length ; ii++) {
			if( !takenIndexes.contains(ii) ) {
				targetArray[targetIndex++] = args[ii];
			}
		}
	
		return targetArray;
	}
	
	
	private final String[] args;
	
	private Map<String, Integer> switchIndexes = new HashMap<>();
	private Set<Integer> takenIndexes  = new HashSet<>();
}
