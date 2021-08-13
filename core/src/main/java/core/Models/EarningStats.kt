package core.Models

import com.google.gson.annotations.Expose
import java.io.Serializable
import java.util.*

class StatsEntry : Serializable {
    var key: String? = null
    var value: Int? = null
}

class EarningStats : Serializable {
    @Expose
    var year_hist:  ArrayList<StatsEntry> ? = null
    @Expose
    var month_hist:  ArrayList<StatsEntry> ? = null
    @Expose
    var day_hist:  ArrayList<StatsEntry> ? = null
}