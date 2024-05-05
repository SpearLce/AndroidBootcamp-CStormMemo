package com.illidancstormrage.cstormmemo.model.network


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OrderResultNullable(
    @SerialName("lattice")
    var lattice: List<Lattice?>?,
    @SerialName("lattice2")
    var lattice2: List<Lattice2?>?
) {
    @Serializable
    data class Lattice(
        @SerialName("json_1best")
        var json1best: String? // {"st":{"sc":"0.00","pa":"0","rt":[{"ws":[{"cw":[
    )

    @Serializable
    data class Lattice2(
        @SerialName("begin")
        var begin: String?, // 190
        @SerialName("end")
        var end: String?, // 1010
        @SerialName("json_1best")
        var json1best: Json1best?, //！Json1best
        @SerialName("lid")
        var lid: String?, // 0
        @SerialName("spk")
        var spk: String? // 段落-0
    ) {
        @Serializable
        data class Json1best(
            @SerialName("st")
            var st: St? //! Json1best.st
        ) {
            @Serializable
            data class St(
                @SerialName("bg")
                var bg: String?, // 190
                @SerialName("ed")
                var ed: String?, // 1010
                @SerialName("pa")
                var pa: String?, // 0
                @SerialName("pt")
                var pt: String?, // reserved
                @SerialName("rl")
                var rl: String?, // 0
                @SerialName("rt")
                var rt: List<Rt?>?, //! Json1best.st.rt
                @SerialName("sc")
                var sc: String?, // 0.00
                @SerialName("si")
                var si: String? // 0
            ) {
                @Serializable
                data class Rt(
                    @SerialName("nb")
                    var nb: String?, // 1
                    @SerialName("nc")
                    var nc: String?, // 1.0
                    @SerialName("ws")
                    var ws: List<W?>? //! Json1best.st.rt[0].ws （nb=1只有一项/未开头其他功能）
                ) {
                    @Serializable
                    data class W(
                        @SerialName("cw")
                        var cw: List<Cw?>?,//! Json1best.st.rt[0].ws[i].cw
                        @SerialName("wb")
                        var wb: Int?, // 1
                        @SerialName("we")
                        var we: Int? // 20
                    ) {
                        @Serializable
                        data class Cw(
                            @SerialName("w")
                            var w: String?, // 这个 //! Json1best.st.rt[0].ws[i].cw[0].w
                            @SerialName("wc")
                            var wc: String?, // 0.0000
                            @SerialName("wp")
                            var wp: String? // n
                        )
                    }
                }
            }
        }
    }
}