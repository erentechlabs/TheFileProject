# The File Project 📁

A comprehensive REST API for file conversion and manipulation built with Spring Boot. This application provides endpoints for converting, resizing, compressing, and manipulating various file formats including images, PDFs, office documents, and media files.

## 📋 Table of Contents

- [Features](#features)
- [Supported File Formats](#supported-file-formats)
- [Prerequisites](#prerequisites)
- [Installation](#installation)
- [Configuration](#configuration)
- [API Endpoints](#api-endpoints)
- [Usage Examples](#usage-examples)
- [Error Handling](#error-handling)
- [Contributing](#contributing)
- [License](#license)

## ✨ Features

### Image Processing
- **Format Conversion**: Convert between PNG, JPG, JPEG, GIF, BMP, and WebP formats
- **Image Resizing**: Resize images with optional aspect ratio preservation
- **Image Compression**: Compress images with customizable quality settings
- **Batch Processing**: Handle multiple image operations efficiently

### PDF Operations
- PDF creation, manipulation, and conversion capabilities
- Integration with Apache PDFBox and iText7

### Office Document Processing
- Support for Microsoft Office formats (Word, Excel, PowerPoint)
- Document conversion and manipulation
- Integration with Apache POI

### Media File Support
- Video format support: MP4, AVI, MOV, MKV
- Audio format support: MP3, WAV, FLAC

## 📁 Supported File Formats

| Category | Supported Formats |
|----------|-------------------|
| **Images** | JPG, JPEG, PNG, GIF, BMP, WebP |
| **PDF** | PDF |
| **Office** | DOCX, DOC, XLSX, XLS, PPTX, PPT |
| **Video** | MP4, AVI, MOV, MKV |
| **Audio** | MP3, WAV, FLAC |

## 🔧 Prerequisites

- **Java**: Version 24 or higher
- **Maven**: Version 3.6+ for dependency management
- **Memory**: Minimum 512MB RAM (recommended 1GB+ for large file processing)

## 🚀 Installation

### Clone the Repository
```
bash git clone https://github.com/erentechlabs/TheFileProject cd TheFileProject
``` 

### Build the Project
```
bash mvn clean install
``` 

### Run the Application
```
bash mvn spring-boot:run
``` 

The application will start on `http://localhost:8080`

## ⚙️ Configuration

### Application Properties

The application can be configured via `application.properties`:
```
properties
# Server Configuration
server.port=8080
# File Upload Configuration
spring.servlet.multipart.max-file-size=50MB spring.servlet.multipart.max-request-size=50MB
# Temporary Directory
file.upload.temp-dir=${java.io.tmpdir}/thefileproject
# Conversion Timeout
file.conversion.timeout-seconds=30
# Logging Configuration
logging.level.com.thefileproject=DEBUG logging.file.name=logs/thefileproject.log
``` 

### Customizable Settings

- **Maximum file size**: Default 50MB (configurable)
- **Conversion timeout**: 30 seconds (configurable)
- **Temporary directory**: System temp directory (configurable)
- **Logging levels**: Configurable per package

## 🛠 API Endpoints

### Image Conversion

| Endpoint | Method | Description | Parameters |
|----------|--------|-------------|------------|
| `/api/v1/convert/image/to-png` | POST | Convert image to PNG | `file` (multipart) |
| `/api/v1/convert/image/to-jpg` | POST | Convert image to JPG | `file` (multipart) |
| `/api/v1/convert/image/to-webp` | POST | Convert image to WebP | `file` (multipart) |

### Image Manipulation

| Endpoint | Method | Description | Parameters |
|----------|--------|-------------|------------|
| `/api/v1/convert/image/resize` | POST | Resize image | `file`, `width`, `height`, `keepAspectRatio` |
| `/api/v1/convert/image/compress` | POST | Compress image | `file`, `quality` (0.1-1.0) |

### Future Endpoints
- PDF operations: `/api/v1/convert/pdf/*`
- Office documents: `/api/v1/convert/office/*`
- Video processing: `/api/v1/convert/video/*`

## 📝 Usage Examples

### Convert Image to PNG
```bash
curl -X POST \
  http://localhost:8080/api/v1/convert/image/to-png \
  -F 'file=@example.jpg'
```
```
### Resize Image
``` bash
curl -X POST \
  http://localhost:8080/api/v1/convert/image/resize \
  -F 'file=@example.jpg' \
  -F 'width=800' \
  -F 'height=600' \
  -F 'keepAspectRatio=true'
```
### Compress Image
``` bash
curl -X POST \
  http://localhost:8080/api/v1/convert/image/compress \
  -F 'file=@example.jpg' \
  -F 'quality=0.8'
```
### Using JavaScript/Frontend
``` javascript
const formData = new FormData();
formData.append('file', fileInput.files[0]);

fetch('/api/v1/convert/image/to-png', {
    method: 'POST',
    body: formData
})
.then(response => response.blob())
.then(blob => {
    const url = window.URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = 'converted.png';
    a.click();
});
```
## ❌ Error Handling
The API provides comprehensive error handling with detailed error messages:
### Common Error Responses
``` json
{
  "timestamp": "2024-01-15T10:30:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "File format .xyz is not supported. Supported formats: jpg, jpeg, png, gif, bmp, webp",
  "path": "/api/v1/convert/image/to-png"
}
```
### Error Codes

| Status Code | Description |
| --- | --- |
| 400 | Bad Request - Invalid file format or parameters |
| 413 | Payload Too Large - File size exceeds limit |
| 415 | Unsupported Media Type - File type not supported |
| 500 | Internal Server Error - Conversion failure |
## 🏗 Architecture
``` 
src/main/java/com/thefileproject/
├── controller/          # REST API endpoints
│   ├── ImageController.java
│   ├── PdfController.java
│   ├── OfficeController.java
│   └── VideoController.java
├── service/            # Business logic
│   ├── ImageService.java
│   ├── PdfService.java
│   ├── OfficeService.java
│   └── VideoService.java
├── exception/          # Custom exceptions and error handling
│   ├── custom_exception_classes/
│   ├── dto/
│   └── GlobalExceptionHandler.java
└── TheFileProjectApplication.java
```
## 🛡 Security Considerations
- File size limits enforced (50MB default)
- File type validation on upload
- Temporary file cleanup
- Error message sanitization
- Request timeout protection

## 🚧 Development Status
- ✅ **Image Processing**: Fully implemented
- 🚧 **PDF Operations**: In development
- 🚧 **Office Documents**: In development
- 🚧 **Video Processing**: Planned
- 🚧 **Audio Processing**: Planned
