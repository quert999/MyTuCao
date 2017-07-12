package bean

data class Bangumi(val banners: List<Banner>,
                   val recommends: List<Pair<Channel,List<Result>>>)