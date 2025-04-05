# Sample SDK NPM - Android

Este proyecto Android demuestra cómo integrar el SDK de VeriDocID en una aplicación nativa de Android usando un `WebView`. Para esta demo el proceso de verificación se inicia desde una pantalla inicial con un botón, y luego se ejecutan llamadas a la API para crear una verificación y obtener la URL del SDK, la cual se despliega dentro del WebView.

## 🚀 ¿Cómo funciona?

1. El usuario pulsa el botón "Probar" en la pantalla inicial.
2. La app realiza una llamada a la API [`createVerification`](https://documenter.getpostman.com/view/13807324/UVXgNJ4f#05085add-95dc-4023-83a9-a24f7277b8d1) para iniciar una nueva verificación.
3. Se obtiene un identificador único (UUID) como respuesta.
4. Con ese UUID, se hace una segunda llamada a la API [`urlSdk`](https://documenter.getpostman.com/view/13807324/UVXgNJ4f#4cf39b05-77f5-44b6-9db5-92f9d93e9a24) para obtener la URL del SDK web.
5. Finalmente, esa URL se carga en un `WebView`, el cual renderiza el SDK completo que permitirá:
   - Verificación de documento de identidad.
   - Prueba de vida (según las opciones seleccionadas).
   - Verificación de IP (según las opciones seleccionadas).

## 🔐 API Keys

- **Solo debes usar la clave privada (Private API Key)** que tiene el siguiente formato:  
  `sk_xxxxxxxxxxxxxxxxxxxxx`

- Esta clave debe ser enviada en las cabeceras de la solicitud HTTP como:

```http
"x-api-key": "sk_xxxxxxxxxxxxxx"
```

## 🔗 Endpoints utilizados

1. Crear Verificación
   
   **POST** `https://veridocid.azure-api.net/api/id/v3/createVerification`
   
3. Obtener URL del SDK

   **POST** `https://veridocid.azure-api.net/api/id/v3/urlSdk`

## 🛡️ Permisos

El flujo solicita el permiso de cámara al capturar los documentos y el rostro lo que se maneja mediante la nueva API (`ActivityResultContracts.RequestPermission`).

> **Nota**: Si el usuario niega el permiso, se muestra un `Toast`, pero se pueden extender este comportamiento para volver a solicitar el permiso si lo desean.

## 📦 Dependencias principales

```bash
# versions
okhttp = "4.12.0"
coroutines = "1.7.3"

# libs.versions.toml
okhttp = { group = "com.squareup.okhttp3", name = "okhttp", version.ref = "okhttp" }
coroutines-android = { group = "org.jetbrains.kotlinx", name = "kotlinx-coroutines-android", version.ref = "coroutines" }
```


## 📝 Notas adicionales

- Se debe reemplazar `<<---- PRIVATE API KEY ---->>` por la clave privada otorgada a su cuenta.
- Se debe reemplazar `<<---- IDENTIFIER ---->>` por un id alfanumerico que le ayudará a identificar su verificación.
