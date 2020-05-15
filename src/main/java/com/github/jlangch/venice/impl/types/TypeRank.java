package com.github.jlangch.venice.impl.types;

public enum TypeRank {

	CONSTANT (0),
	
	INTEGER (10), 
	LONG (12),
	DOUBLE (13),
	BIGDECIMAL (14),

	STRING (20),
	CHAR (21),
	
	KEYWORD (30),
	SYMBOL (31),
	
	BYTEBUFFER (40),	
	JAVAOBJECT (41),
	CUSTOMTYPE (42),
	
	ATOM (50),
	VOLATILE (51),
	THREADLOCAL (52),
	
	FUNCTION (60),
	MULTI_ARITY_FUNCTION (61),
	MULTI_FUNCTION (62),
	
	JUST (70), 
	
	
	LIST (100),	
	JAVALIST (101),
	MUTABLELIST (102),
	
	VECTOR (110),	
	
	HASHMAP (120),
	ORDEREDMAP (121),
	SORTEDMAP (122),
	JAVAMAP (123),
	MAPENTRY (124),
	MUTABLEMAP (125),
	
	HASHSET (130),
	SORTEDSET (131),
	JAVASET (132),
	MUTABLESET (133),
	
	QUEUE (140),
	STACK (141);
	

	private TypeRank(final int rank) {
		this.rank = rank;
	}

	public int getRank() {
		return rank;
	}
	
	private final int rank;
}
