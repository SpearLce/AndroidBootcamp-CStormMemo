package com.illidancstormrage.utils.database.room.condition

class QueryWrapper(
    //储存条件
    private val conditions: MutableList<Condition> = mutableListOf()
) : AbstractWrapper() {


    override fun eq(column: String, value: Any): AbstractWrapper {
        conditions.add( //保存条件
            Condition(
                column = column,
                operator = "=",
                values = listOf(value),
                conjunction = CONJUNCTION_AND
            )
        )
        return this //返回当前包装类 - 适用链式调用
    }

    override fun eq(conjunction: String, column: String, value: Any): AbstractWrapper {
        conditions.add( //保存条件
            Condition(
                column = column,
                operator = "=",
                values = listOf(value),
                conjunction = conjunction
            )
        )
        return this //返回当前包装类 - 适用链式调用
    }

    override fun ne(column: String, value: Any): AbstractWrapper {
        conditions.add(
            Condition(
                column = column,
                operator = "<>",
                values = listOf(value),
                conjunction = CONJUNCTION_AND
            )
        )
        return this
    }

    override fun ne(conjunction: String, column: String, value: Any): AbstractWrapper {
        conditions.add(
            Condition(
                column = column,
                operator = "<>",
                values = listOf(value),
                conjunction = conjunction
            )
        )
        return this
    }

    override fun gt(column: String, value: Any): AbstractWrapper {
        conditions.add(
            Condition(
                column = column,
                operator = ">",
                values = listOf(value),
                conjunction = CONJUNCTION_AND
            )
        )
        return this
    }

    override fun gt(conjunction: String, column: String, value: Any): AbstractWrapper {
        conditions.add(
            Condition(
                column = column,
                operator = ">",
                values = listOf(value),
                conjunction = conjunction
            )
        )
        return this
    }

    override fun ge(column: String, value: Any): AbstractWrapper {
        conditions.add(
            Condition(
                column = column,
                operator = ">=",
                values = listOf(value),
                conjunction = CONJUNCTION_AND
            )
        )
        return this
    }

    override fun ge(conjunction: String, column: String, value: Any): AbstractWrapper {
        conditions.add(
            Condition(
                column = column,
                operator = ">=",
                values = listOf(value),
                conjunction = conjunction
            )
        )
        return this
    }

    override fun lt(column: String, value: Any): AbstractWrapper {
        conditions.add(
            Condition(
                column = column,
                operator = "<",
                values = listOf(value),
                conjunction = CONJUNCTION_AND
            )
        )
        return this
    }

    override fun lt(conjunction: String, column: String, value: Any): AbstractWrapper {
        conditions.add(
            Condition(
                column = column,
                operator = "<",
                values = listOf(value),
                conjunction = conjunction
            )
        )
        return this
    }

    override fun le(column: String, value: Any): AbstractWrapper {
        conditions.add(
            Condition(
                column = column,
                operator = "<=",
                values = listOf(value),
                conjunction = CONJUNCTION_AND
            )
        )
        return this
    }

    override fun le(conjunction: String, column: String, value: Any): AbstractWrapper {
        conditions.add(
            Condition(
                column = column,
                operator = "<=",
                values = listOf(value),
                conjunction = conjunction
            )
        )
        return this
    }

    override fun between(column: String, value1: Any, value2: Any): AbstractWrapper {
        conditions.add(
            Condition(
                column = column,
                operator = "BETWEEN",
                values = listOf(value1, value2),
                conjunction = CONJUNCTION_AND
            )
        )
        return this
    }

    override fun between(
        conjunction: String,
        column: String,
        value1: Any,
        value2: Any
    ): AbstractWrapper {
        conditions.add(
            Condition(
                column = column,
                operator = "BETWEEN",
                values = listOf(value1, value2),
                conjunction = conjunction
            )
        )
        return this
    }

    override fun notBetween(column: String, value1: Any, value2: Any): AbstractWrapper {
        conditions.add(
            Condition(
                column = column,
                operator = "NOT_BETWEEN",
                values = listOf(value1, value2),
                conjunction = CONJUNCTION_AND
            )
        )
        return this
    }

    override fun notBetween(
        conjunction: String,
        column: String,
        value1: Any,
        value2: Any
    ): AbstractWrapper {
        conditions.add(
            Condition(
                column = column,
                operator = "NOT_BETWEEN",
                values = listOf(value1, value2),
                conjunction = conjunction
            )
        )
        return this
    }

    override fun like(column: String, value: Any): AbstractWrapper {
        conditions.add(
            Condition(
                column = column,
                operator = "LIKE",
                values = listOf(value)
            )
        )
        return this
    }

    override fun like(conjunction: String, column: String, value: Any): AbstractWrapper {
        conditions.add(
            Condition(
                column = column,
                operator = "LIKE",
                values = listOf(value),
                conjunction = conjunction
            )
        )
        return this
    }

    override fun likeLeft(column: String, value: Any): AbstractWrapper {
        conditions.add(
            Condition(
                column = column,
                operator = "LIKE_LEFT",
                values = listOf(value)
            )
        )
        return this
    }

    override fun likeLeft(conjunction: String, column: String, value: Any): AbstractWrapper {
        conditions.add(
            Condition(
                column = column,
                operator = "LIKE_LEFT",
                values = listOf(value),
                conjunction = conjunction
            )
        )
        return this
    }

    override fun likeRight(column: String, value: Any): AbstractWrapper {
        conditions.add(
            Condition(
                column = column,
                operator = "LIKE_RIGHT",
                values = listOf(value)
            )
        )
        return this
    }

    override fun likeRight(conjunction: String, column: String, value: Any): AbstractWrapper {
        conditions.add(
            Condition(
                column = column,
                operator = "LIKE_RIGHT",
                values = listOf(value),
                conjunction = conjunction
            )
        )
        return this
    }

    override fun notLikeLeft(column: String, value: Any): AbstractWrapper {
        conditions.add(
            Condition(
                column = column,
                operator = "NOT_LIKE_LEFT",
                values = listOf(value)
            )
        )
        return this
    }

    override fun notLikeLeft(conjunction: String, column: String, value: Any): AbstractWrapper {
        conditions.add(
            Condition(
                column = column,
                operator = "NOT_LIKE_LEFT",
                values = listOf(value),
                conjunction = conjunction
            )
        )
        return this
    }

    override fun notLikeRight(column: String, value: Any): AbstractWrapper {
        conditions.add(
            Condition(
                column = column,
                operator = "NOT_LIKE_RIGHT",
                values = listOf(value)
            )
        )
        return this
    }

    override fun notLikeRight(conjunction: String, column: String, value: Any): AbstractWrapper {
        conditions.add(
            Condition(
                column = column,
                operator = "NOT_LIKE_RIGHT",
                values = listOf(value),
                conjunction = conjunction
            )
        )
        return this
    }

    override fun isNull(column: String): AbstractWrapper {
        conditions.add(
            Condition(
                column = column,
                operator = "NULL",
                values = listOf(-1)
            )
        )
        return this
    }

    override fun isNull(conjunction: String, column: String): AbstractWrapper {
        conditions.add(
            Condition(
                column = column,
                operator = "NULL",
                values = listOf(-1),
                conjunction = conjunction
            )
        )
        return this
    }

    /*override fun orderByAsc(column: String): AbstractWrapper {
        conditions.add(
            Condition(
                column = column,
                operator = "ORDER_BY_ASC",
                values = listOf(-1)
            )
        )
        return this
    }

    override fun orderByAsc(conjunction: String, column: String): AbstractWrapper {
        conditions.add(
            Condition(
                column = column,
                operator = "ORDER_BY_ASC",
                values = listOf(-1),
                conjunction = conjunction
            )
        )
        return this
    }

    override fun orderByDesc(column: String): AbstractWrapper {
        conditions.add(
            Condition(
                column = column,
                operator = "ORDER_BY_DESC",
                values = listOf(-1)
            )
        )
        return this
    }

    override fun orderByDesc(conjunction: String, column: String): AbstractWrapper {
        conditions.add(
            Condition(
                column = column,
                operator = "ORDER_BY_DESC",
                values = listOf(-1),
                conjunction = conjunction
            )
        )
        return this
    }*/


    override fun toString(): String {
        return conditions.toString()
    }


    override fun getWhereClause(): String {
        val clauseBuilder = StringBuilder()
        var isFirstClause = true

        conditions.forEachIndexed { _, condition ->

            if (isFirstClause && clauseBuilder.isEmpty()) {
                // 首个条件句
                clauseBuilder.append(assembleClause(condition))
                isFirstClause = false
            } else {
                // 后续条件句
                when (condition.conjunction) {
                    CONJUNCTION_AND -> {
                        clauseBuilder.append(" AND ")
                        clauseBuilder.append(assembleClause(condition))
                    }

                    CONJUNCTION_OR -> {
                        clauseBuilder.append(" OR ")
                        clauseBuilder.append(assembleClause(condition))
                    }

                    else -> {

                    }
                }
            }
        }



        return if (clauseBuilder.isEmpty()) "" else "WHERE $clauseBuilder"
    }

    /**
     * 组装Condition属性为字符串子句 -> 例: (id = 1)
     * @param condition Condition
     * @return String
     */
    private fun assembleClause(condition: Condition): String {
        val clauseBuilder = StringBuilder()
        val value = addQuotesIfNeeded(condition.values.singleOrNull())
        val column = condition.column
        when (condition.operator) {
            "=" -> clauseBuilder.append("$column = $value")
            ">" -> clauseBuilder.append("$column > $value")
            ">=" -> clauseBuilder.append("$column >= $value")
            "<" -> clauseBuilder.append("$column < $value")
            "<=" -> clauseBuilder.append("$column <= $value")
            "<>" -> clauseBuilder.append("$column <> $value")
            "BETWEEN" -> clauseBuilder.append("$column BETWEEN ${condition.values[0]} AND ${condition.values[1]}")
            "NOT_BETWEEN" -> clauseBuilder.append("$column NOT BETWEEN ${condition.values[0]} AND ${condition.values[1]}")
            "LIKE" -> clauseBuilder.append("$column LIKE '%${condition.values.singleOrNull()}%'")
            "LIKE_LEFT" -> clauseBuilder.append("$column LIKE '%${condition.values.singleOrNull()}'")
            "LIKE_RIGHT" -> clauseBuilder.append("$column LIKE '${condition.values.singleOrNull()}%'")
            "NOT_LIKE_LEFT" -> clauseBuilder.append("$column NOT LIKE '%${condition.values.singleOrNull()}'")
            "NOT_LIKE_RIGHT" -> clauseBuilder.append("$column NOT LIKE '${condition.values.singleOrNull()}%'")
            "NULL" -> clauseBuilder.append("$column IS NULL")
            "NOT_NULL" -> clauseBuilder.append("$column IS NOT NULL")
            "ORDER_BY_ASC" -> clauseBuilder.append("ORDER BY $column ASC") //?
            "ORDER_BY_DESC" -> clauseBuilder.append("ORDER BY $column DESC")//?
            else -> {}
        }
        if (condition.values.isNotEmpty()) {
            clauseBuilder.apply {
                insert(0, "(")
                append(")")
            }
        }

        return clauseBuilder.toString()
    }

    /**
     * 根据value类型添加单引号' '引用 - '1' is String
     * @param value Any? 字段值
     * @return String 是否添加单引号后字符串
     */
    private fun addQuotesIfNeeded(value: Any?): String {
        return when (value) {
            is String -> "'$value'"
            is Number,
            is Boolean -> value.toString()

            else -> "${value?.toString()}" //null - ''
        }
    }

}