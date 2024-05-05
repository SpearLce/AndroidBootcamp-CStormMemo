package com.illidancstormrage.utils.database.room.condition

//构造 where
abstract class AbstractWrapper {

    companion object {
        const val CONJUNCTION_AND = "and"
        const val CONJUNCTION_OR = "or"


    }

    /**
     * 相等 = 条件子句
     * * 添加子句条件到conditions集合中
     * * conjunction 连词默认 and
     * @param column String 字段名
     * @param value Any 字段值
     * @return AbstractWrapper 自身条件包装类
     */
    abstract fun eq(column: String, value: Any): AbstractWrapper

    /**
     * 相等 = 条件子句
     * * 添加子句条件到conditions集合中
     * @param conjunction String 子句连接词 / 首句无效
     * @param column String 字段名
     * @param value Any 字段值
     * @return AbstractWrapper 自身条件包装类
     */
    abstract fun eq(conjunction: String, column: String, value: Any): AbstractWrapper

    /**
     * AND column <> value
     * @param column String
     * @param value Any
     * @return AbstractWrapper
     */
    abstract fun ne(column: String, value: Any): AbstractWrapper

    /**
     * OR column <> value
     * @param conjunction String
     * @param column String
     * @param value Any
     * @return AbstractWrapper
     */
    abstract fun ne(conjunction: String, column: String, value: Any): AbstractWrapper

    /**
     * [>]
     * @param column String
     * @param value Any
     * @return AbstractWrapper
     */
    abstract fun gt(column: String, value: Any): AbstractWrapper
    abstract fun gt(conjunction: String, column: String, value: Any): AbstractWrapper

    /**
     * [>=]
     * @param column String
     * @param value Any
     * @return AbstractWrapper
     */
    abstract fun ge(column: String, value: Any): AbstractWrapper
    abstract fun ge(conjunction: String, column: String, value: Any): AbstractWrapper

    /**
     * [<]
     * @param column String
     * @param value Any
     * @return AbstractWrapper
     */
    abstract fun lt(column: String, value: Any): AbstractWrapper
    abstract fun lt(conjunction: String, column: String, value: Any): AbstractWrapper

    /**
     * [<=]
     * @param column String
     * @param value Any
     * @return AbstractWrapper
     */
    abstract fun le(column: String, value: Any): AbstractWrapper
    abstract fun le(conjunction: String, column: String, value: Any): AbstractWrapper

    /**
     * [between] - [value1] and [value2]
     * @param column String
     * @param value1 Any
     * @param value2 Any
     * @return AbstractWrapper
     */
    abstract fun between(column: String, value1: Any, value2: Any): AbstractWrapper
    abstract fun between(
        conjunction: String,
        column: String,
        value1: Any,
        value2: Any
    ): AbstractWrapper

    /**
     * not between - [value1] and [value2]
     * @param column String
     * @param value1 Any
     * @param value2 Any
     * @return AbstractWrapper
     */
    abstract fun notBetween(column: String, value1: Any, value2: Any): AbstractWrapper
    abstract fun notBetween(
        conjunction: String,
        column: String,
        value1: Any,
        value2: Any
    ): AbstractWrapper

    /**
     * LIKE '%值%'
     * * 例: like("name", "王")--->name like '%王%'
     * @param column String
     * @param value Any
     * @return AbstractWrapper
     */

    abstract fun like(column: String, value: Any): AbstractWrapper
    abstract fun like(conjunction: String, column: String, value: Any): AbstractWrapper

    abstract fun likeLeft(column: String, value: Any): AbstractWrapper
    abstract fun likeLeft(conjunction: String, column: String, value: Any): AbstractWrapper
    abstract fun likeRight(column: String, value: Any): AbstractWrapper
    abstract fun likeRight(conjunction: String, column: String, value: Any): AbstractWrapper
    abstract fun notLikeLeft(column: String, value: Any): AbstractWrapper
    abstract fun notLikeLeft(conjunction: String, column: String, value: Any): AbstractWrapper
    abstract fun notLikeRight(column: String, value: Any): AbstractWrapper
    abstract fun notLikeRight(conjunction: String, column: String, value: Any): AbstractWrapper

    abstract fun isNull(column: String): AbstractWrapper
    abstract fun isNull(conjunction: String, column: String): AbstractWrapper

    /**
     * 升序
     * @param column String
     * @return AbstractWrapper
     */
    //abstract fun orderByAsc(column: String): AbstractWrapper
    //abstract fun orderByAsc(conjunction: String, column: String): AbstractWrapper

    /**
     * 降序
     * @param column String
     * @return AbstractWrapper
     */
    //abstract fun orderByDesc(column: String): AbstractWrapper
    //abstract fun orderByDesc(conjunction: String, column: String): AbstractWrapper

    /**
     * 获取组装后的WHERE子句 -> 例如：WHERE id=1 AND id=2 OR id=4
     * @return String
     */
    abstract fun getWhereClause(): String

}