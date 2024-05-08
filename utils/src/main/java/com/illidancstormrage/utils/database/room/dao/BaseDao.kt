package com.illidancstormrage.utils.database.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.RawQuery
import androidx.room.Update
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQuery
import com.illidancstormrage.utils.database.room.condition.AbstractWrapper
import com.illidancstormrage.utils.extensions.simpleName
import com.illidancstormrage.utils.log.LogUtil
import kotlin.reflect.full.superclasses

//@Dao
abstract class BaseDao<T : Any> {


    /**
     * 通过反射获取类名 - 即表名
     * * 确保注解@Entity.TableName所标注的表名与实体类名与一致，
     * 即使用本工具时不要使用注解指定表名，确保类名即表名
     */
    private val tableName: String
        get() {
            //superclasses = KClass<*> - list[]
            //supertypes = KType(有泛型参数<T>) - list[]
            val baseType = javaClass.kotlin.superclasses[0].supertypes[0]
            val typeProjectionArgument = baseType.arguments[0] //获取泛型投影 - kotlin概念
            return typeProjectionArgument.simpleName
        }

    /**
     * 插入单个对象
     * * onConflict = OnConflictStrategy.REPLACE
     * * 冲突策略：相同主键，以新换旧
     * * 开发备忘录时，带外键有bug?先写 ignore - 而且 insertOne 与 insert 只能存在一个否则相互影响
     * @param obj T 插入对象
     * @return Long 返回插入行id - rowId
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(obj: T): Long

    /**
     * @param objs Array<out T> 插入对象的可变参数
     * @return LongArray? Long数组，元素对应rowId
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(vararg objs: T): LongArray?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(objList: List<T>): List<Long>

    /**
     *
     * @param obj T 含准确id的对象
     * @return Int 成功更新的行数
     */
    @Delete
    abstract fun delete(obj: T): Int

    @Delete
    abstract fun delete(vararg objs: T): Int

    @Delete
    abstract fun delete(objList: List<T>): Int


    /**
     * [obj] 要设置存在的id，才能update
     * @param obj T
     * @return Int
     */
    @Update
    abstract fun update(obj: T): Int

    @Update
    abstract fun update(vararg objs: T): Int

    @Update
    abstract fun update(objList: List<T>): Int


    /**
     *
     * @param id Long 通过id查询
     * @return T? 查询成功到返回 <T>，查询失败返回 null
     */
    fun selectById(id: Long): T {
        val sql = SimpleSQLiteQuery(
            query = "select * from $tableName where id = $id"
        )
        return queryOne(sql)
    }


    /**
     *  查询整个表
     * @return List<T> 查询整表结果
     */
    fun selectAll(): List<T> {
        val sql = SimpleSQLiteQuery(
            query = "select * from $tableName"
        )
        return queryList(sql)
    }


    /**
     * 批量通过id查询
     * @param ids LongArray 批量 id
     * @return List<T> 返回结果集
     */
    fun selectByIds(vararg ids: Long): List<T> {
        /**
         * 拼凑示例：(2,1,6,3)
         */
        val idSet = ids.joinToString(prefix = "(", postfix = ")", separator = ",")

        val sql = SimpleSQLiteQuery(
            query = "select * from $tableName where id in $idSet"
        )
        return queryList(sql)
    }


    fun selectByIds(ids: List<Long>): List<T> {
        /**
         * 拼凑示例：(2,1,6,3)
         */
        val idSet = ids.joinToString(prefix = "(", postfix = ")", separator = ",")

        val sql = SimpleSQLiteQuery(
            query = "select * from $tableName where id in $idSet"
        )
        return queryList(sql)
    }

    /**
     * 根据条件[wrapper] 查询
     * @param wrapper AbstractWrapper
     * @return [T] 结果 可null
     */
    fun selectOne(wrapper: AbstractWrapper): T?{
        val whereClause = wrapper.getWhereClause()
        val sql: SimpleSQLiteQuery = if (whereClause.isNotEmpty()) {
            LogUtil.e(msg = "select * from $tableName $whereClause")
            SimpleSQLiteQuery(
                query = "select * from $tableName $whereClause"
            )
        } else {
            SimpleSQLiteQuery(
                query = "select * from $tableName"
            )
        }
        return queryOneTest(sql)
    }

    /**
     * 根据条件[wrapper] 查询
     * @param wrapper AbstractWrapper
     * @return [List] 结果集
     */
    fun selectList(wrapper: AbstractWrapper): List<T> {
        val whereClause = wrapper.getWhereClause()
        val sql: SimpleSQLiteQuery = if (whereClause.isNotEmpty()) {
            LogUtil.e(msg = "select * from $tableName $whereClause")
            SimpleSQLiteQuery(
                query = "select * from $tableName $whereClause"
            )
        } else {
            SimpleSQLiteQuery(
                query = "select * from $tableName"
            )
        }
        return queryList(sql)
    }


    @RawQuery
    abstract fun queryOne(query: SupportSQLiteQuery): T

    @RawQuery
    abstract fun queryOneTest(query: SupportSQLiteQuery): T? //?可写可不写，如果返回没有就是空

    @RawQuery
    abstract fun queryList(query: SupportSQLiteQuery): List<T>

}