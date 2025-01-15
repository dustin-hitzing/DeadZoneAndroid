import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

@Composable
fun PermissionHandler(
    onAllPermissionsGranted: @Composable () -> Unit,
    onSomePermissionsDenied: @Composable (deniedPermissions: List<String>) -> Unit
) {
    val permissions = listOf(
        android.Manifest.permission.ACCESS_FINE_LOCATION,
        android.Manifest.permission.ACCESS_COARSE_LOCATION,
        android.Manifest.permission.ACCESS_NETWORK_STATE,
        android.Manifest.permission.ACCESS_WIFI_STATE
    )

    // State to track denied permissions
    val deniedPermissionsState = remember { mutableStateOf<List<String>?>(null) }

    val permissionsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissionsResult ->
            val deniedPermissions = permissionsResult.filterValues { !it }.keys.toList()
            if (deniedPermissions.isEmpty()) {
                deniedPermissionsState.value = emptyList() // All granted
            } else {
                deniedPermissionsState.value = deniedPermissions // Some denied
            }
        }
    )

    // Launch the permissions request when this composable is displayed
    LaunchedEffect(Unit) {
        permissionsLauncher.launch(permissions.toTypedArray())
    }

    // React to permission state changes
    deniedPermissionsState.value?.let { deniedPermissions ->
        if (deniedPermissions.isEmpty()) {
            onAllPermissionsGranted()
        } else {
            onSomePermissionsDenied(deniedPermissions)
        }
    }
}
