# The File Project 📁

A comprehensive REST API for file conversion and manipulation built with Spring Boot. It provides endpoints for converting, resizing, compressing, and processing images, PDFs, Office documents, and media files.

## 📋 Table of Contents

- [Features](#features)
- [Supported File Formats](#supported-file-formats)
- [Prerequisites](#prerequisites)
- [Installation](#installation)
- [Configuration](#configuration)
- [API Endpoints](#api-endpoints)
- [Usage Examples](#usage-examples)
- [Error Handling](#error-handling)
- [Architecture](#architecture)
- [Security Considerations](#security-considerations)
- [Development Status](#development-status)
- [Contributing](#contributing)
- [License](#license)

## ✨ Features

### Image Processing
- Format conversion: PNG, JPG, JPEG, GIF, BMP, WebP
- Resizing with optional aspect ratio preservation
- Compression with configurable quality
- Batch-friendly design

### PDF Operations
- PDF creation, manipulation, and conversion
- TXT → PDF, DOCX → PDF, XLSX/XLS → PDF, and PDF → PDF (copy)
- Powered by Apache PDFBox (and converters for DOCX/Excel to PDF)
- Note: OCR is not included; scanned PDFs without text will not yield extracted text

### Office Document Processing
- Word, Excel conversions and basic manipulation
- PDF → DOCX (text-only), DOCX ↔ XLSX conversions, DOCX/XLSX copy
- Integration with Apache POI

### Media File Support
- Video formats: MP4, AVI, MOV, MKV
- Audio formats: MP3, WAV, FLAC

## 📁 Supported File Formats

| Category | Formats |
|----------|---------|
| Images | JPG, JPEG, PNG, GIF, BMP, WebP |
| PDF | PDF |
| Office | DOCX, DOC, XLSX, XLS, PPTX, PPT |
| Video | MP4, AVI, MOV, MKV |
| Audio | MP3, WAV, FLAC |

## 🔧 Prerequisites

- Java: 24 or higher
- Maven: 3.6+
- Memory: 512MB minimum (1GB+ recommended for large files)

## 🚀 Installation

### Clone the repository
``` bash
bash git clone https://github.com/erentechlabs/TheFileProject
cd TheFileProject
``` 

### Build the project
``` bash
bash mvn clean install
``` 

### Run the application
``` bash
bash mvn spring-boot:run
``` 

The application starts at: http://localhost:8080

## ⚙️ Configuration

Create or edit `src/main/resources/application.properties`:
``` 
properties
# Server Configuration
server.port=8080
# File Upload Configuration
spring.servlet.multipart.max-file-size=300MB 
spring.servlet.multipart.max-request-size=300MB
# Temporary Directory
file.upload.temp-dir=${java.io.tmpdir}/thefileproject
# Conversion Timeout
file.conversion.timeout-seconds=30
# Logging Configuration
logging.level.com.thefileproject=DEBUG 
logging.file.name=logs/thefileproject.log
``` 

Customizable settings:
- Maximum file size (default: 50MB)
- Conversion timeout (default: 30s)
- Temporary directory
- Logging levels per package

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
| `/api/v1/convert/image/compress` | POST | Compress image | `file`, `quality` (0.1–1.0) |

### Future Endpoints
- Video processing: `/api/v1/convert/video/*`

## 📝 Usage Examples

### Convert Image to PNG
``` bash
bash curl -X POST
[http://localhost:8080/api/v1/convert/image/to-png](http://localhost:8080/api/v1/convert/image/to-png)
-F 'file=@example.jpg'
-o converted.png
``` 

### Resize Image
``` bash
bash curl -X POST
[http://localhost:8080/api/v1/convert/image/resize](http://localhost:8080/api/v1/convert/image/resize)
-F 'file=@example.jpg'
-F 'width=800'
-F 'height=600'
-F 'keepAspectRatio=true'
-o resized.jpg
``` 

### Compress Image
``` bash
bash curl -X POST
[http://localhost:8080/api/v1/convert/image/compress](http://localhost:8080/api/v1/convert/image/compress)
-F 'file=@example.jpg'
-F 'quality=0.8'
-o compressed.jpg
``` 

### Using JavaScript (browser)
``` javascript
javascript const formData = new FormData(); 
formData.append('file', fileInput.files[0]);
fetch('/api/v1/convert/image/to-png', { method: 'POST', body: formData })
.then(res => res.blob())
.then(blob => { const url = URL.createObjectURL(blob); 
const a = document.createElement('a'); 
a.href = url; 
a.download = 'converted.png'; 
a.click(); 
URL.revokeObjectURL(url); });
``` 

## ❌ Error Handling

Typical error response:
``` json 
{ 
"timestamp": "2024-01-15T10:30:00Z", 
"status": 400, "error": "Bad Request", 
"message": "File format .xyz is not supported. Supported formats: jpg, jpeg, png, gif, bmp, webp", 
"path": "/api/v1/convert/image/to-png" 
}
``` 

Error codes:
- 400: Bad Request — Invalid file format or parameters
- 413: Payload Too Large — File size exceeds limit
- 415: Unsupported Media Type — File type not supported
- 500: Internal Server Error — Conversion failure

## 🏗 Architecture
```
text src/main/java/com/thefileproject/ 
├── controller/ # REST API endpoints 
│ ├── ImageController.java 
│ ├── PdfController.java 
│ ├── OfficeController.java 
│ └── VideoController.java 
├── service/ # Business logic 
│ ├── ImageService.java 
│ ├── PdfService.java 
│ ├── OfficeService.java 
│ └── VideoService.java 
├── exception
/ # Custom exceptions and error handling 
│ ├── custom_exception_classes/ 
│ ├── dto/ 
│ └── GlobalExceptionHandler.java 
└── TheFileProjectApplication.java
``` 

## 🛡 Security Considerations
- Enforced file size limits (default 50MB)
- File type validation
- Temporary file cleanup
- Error message sanitization
- Request timeout protection

## 🚧 Development Status
- ✅ Image Processing: Fully implemented
- ✅ PDF Operations: Implemented (basic conversions)
- ✅ Office Documents: Implemented (basic conversions)
- 🗓️ Video Processing: Planned
- 🗓️ Audio Processing: Planned


