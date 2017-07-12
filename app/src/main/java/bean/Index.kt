package bean

data class Index(val banners: List<Banner>,
                 val recommends: List<Pair<Channel,List<Result>>>)