package uz.idea.domain.models.tasNifProduct

data class Data(
    val attributeName: String,
    val brandName: String,
    val className: String,
    val commonUnitCode: Any,
    val commonUnitName: Any,
    val createdAt: String,
    val groupName: String,
    val internationalCode: String,
    val isActive: Int,
    val label: Int,
    val mxikCode: String,
    val mxikFullName: String,
    val packageNames: List<PackageName>,
    val pkey: String,
    val positionName: String,
    val subPositionName: String,
    val tin: String,
    val unitCode: Any,
    val unitName: Any,
    val unitValue: Any,
    val units: Any,
    val usePackage: String,
    val usePackage2: String
)