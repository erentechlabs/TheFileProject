# The File Project 📁

A comprehensive REST API for file conversion and manipulation built with Spring Boot. Convert and process images, PDFs, and Office documents with ease through simple HTTP endpoints.

## 📋 Table of Contents

- [Features](#-features)
- [Supported File Formats](#-supported-file-formats)
- [Prerequisites](#-prerequisites)
- [Installation](#-installation)
- [Configuration](#️-configuration)
- [API Endpoints](#-api-endpoints)
- [Usage Examples](#-usage-examples)
- [Error Handling](#-error-handling)
- [Architecture](#-architecture)
- [Technology Stack](#-technology-stack)
- [Security Considerations](#-security-considerations)
- [Development Status](#-development-status)

## ✨ Features

### Image Processing
- **Format Conversion**: Convert between PNG, JPG, JPEG, GIF, BMP, and WebP
- **Resizing**: Resize images with optional aspect ratio preservation
- **Compression**: Reduce file size with configurable quality (0.1–1.0)
- **Batch Processing**: Designed for efficient batch operations

### PDF Operations
- **Document to PDF**: Convert TXT, DOCX, XLSX, and XLS files to PDF
- **PDF Copying**: Create PDF copies with validation
- **Libraries**: Powered by Apache PDFBox, iText7, and OpenSagres converters
- **Limitation**: OCR not included; scanned PDFs without text layers cannot be processed

### Office Document Processing
- **Cross-Format Conversion**: PDF ↔ DOCX, DOCX ↔ XLSX
- **Document Manipulation**: Extract, modify, and convert Office documents
- **Libraries**: Apache POI for Office file handling

## 📁 Supported File Formats

| Category | Input Formats | Output Formats |
|----------|---------------|----------------|
| **Images** | JPG, JPEG, PNG, GIF, BMP, WebP | PNG, JPG, WebP |
| **PDF** | TXT, DOCX, XLSX, XLS, PDF | PDF |
| **Office** | PDF, DOCX, XLSX | DOCX, XLSX |

## 🔧 Prerequisites

- **Java**: 24 or higher
- **Maven**: 3.6+
- **Memory**: 512MB minimum (1GB+ recommended for large files)
- **OS**: Windows, Linux, macOS

## 🚀 Installation

### 1. Clone the Repository
```bash
git clone https://github.com/erentechlabs/TheFileProject
cd TheFileProject
```

### 2. Build the Project
```bash
mvn clean install
```

### 3. Run the Application
```bash
mvn spring-boot:run
```

The application will start at **http://localhost:8080**

## ⚙️ Configuration

Create or edit `src/main/resources/application.properties`:

```properties
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

### Configurable Options
- **Maximum file size**: Default 300MB
- **Conversion timeout**: Default 30 seconds
- **Temporary directory**: System temp directory by default
- **Logging levels**: Adjust per package

## 🛠 API Endpoints

### Image Endpoints

| Endpoint | Method | Description | Parameters |
|----------|--------|-------------|------------|
| `/api/v1/convert/image/to-png` | POST | Convert image to PNG | `file` (multipart) |
| `/api/v1/convert/image/to-jpg` | POST | Convert image to JPG | `file` (multipart) |
| `/api/v1/convert/image/to-webp` | POST | Convert image to WebP | `file` (multipart) |
| `/api/v1/convert/image/resize` | POST | Resize image | `file`, `width`, `height`, `keepAspectRatio` (default: true) |
| `/api/v1/convert/image/compress` | POST | Compress image | `file`, `quality` (0.1–1.0, default: 0.8) |

### PDF Endpoints

| Endpoint | Method | Description | Parameters |
|----------|--------|-------------|------------|
| `/api/v1/convert/pdf/txt-to-pdf` | POST | Convert TXT to PDF | `file` (multipart) |
| `/api/v1/convert/pdf/docx-to-pdf` | POST | Convert DOCX to PDF | `file` (multipart) |
| `/api/v1/convert/pdf/xlsx-to-pdf` | POST | Convert XLSX to PDF | `file` (multipart) |
| `/api/v1/convert/pdf/xls-to-pdf` | POST | Convert XLS to PDF | `file` (multipart) |
| `/api/v1/convert/pdf/pdf-to-pdf` | POST | Copy/validate PDF | `file` (multipart) |

### Office Endpoints

| Endpoint | Method | Description | Parameters |
|----------|--------|-------------|------------|
| `/api/v1/convert/office/pdf-to-docx` | POST | Convert PDF to DOCX | `file` (multipart) |
| `/api/v1/convert/office/docx-to-xlsx` | POST | Convert DOCX to XLSX | `file` (multipart) |
| `/api/v1/convert/office/xlsx-to-docx` | POST | Convert XLSX to DOCX | `file` (multipart) |

## 📝 Usage Examples

### Convert Image to PNG
```bash
curl -X POST http://localhost:8080/api/v1/convert/image/to-png \
  -F "file=@example.jpg" \
  -o converted.png
```

### Resize Image
```bash
curl -X POST http://localhost:8080/api/v1/convert/image/resize \
  -F "file=@example.jpg" \
  -F "width=800" \
  -F "height=600" \
  -F "keepAspectRatio=true" \
  -o resized.jpg
```

### Compress Image
```bash
curl -X POST http://localhost:8080/api/v1/convert/image/compress \
  -F "file=@example.jpg" \
  -F "quality=0.8" \
  -o compressed.jpg
```

### Convert DOCX to PDF
```bash
curl -X POST http://localhost:8080/api/v1/convert/pdf/docx-to-pdf \
  -F "file=@document.docx" \
  -o document.pdf
```

### Convert PDF to DOCX
```bash
curl -X POST http://localhost:8080/api/v1/convert/office/pdf-to-docx \
  -F "file=@document.pdf" \
  -o document.docx
```

### JavaScript Example (Browser)
```javascript
const formData = new FormData();
formData.append('file', fileInput.files[0]);

fetch('http://localhost:8080/api/v1/convert/image/to-png', {
  method: 'POST',
  body: formData
})
.then(res => res.blob())
.then(blob => {
  const url = URL.createObjectURL(blob);
  const a = document.createElement('a');
  a.href = url;
  a.download = 'converted.png';
  a.click();
  URL.revokeObjectURL(url);
});
```

## ❌ Error Handling

### Standard Error Response
```json
{
  "timestamp": "2025-01-15T10:30:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "File format .xyz is not supported. Supported formats: jpg, jpeg, png, gif, bmp, webp",
  "path": "/api/v1/convert/image/to-png"
}
```

### HTTP Status Codes
- **400 Bad Request**: Invalid file format or parameters
- **413 Payload Too Large**: File size exceeds configured limit
- **415 Unsupported Media Type**: File type not supported
- **500 Internal Server Error**: Conversion or processing failure

## 🏗 Architecture

```
src/main/java/com/thefileproject/
├── controller/              # REST API endpoints
│   ├── ImageController.java
│   ├── PdfController.java
│   ├── OfficeController.java
│   └── VideoController.java
├── service/                 # Business logic
│   ├── ImageService.java
│   ├── PdfService.java
│   ├── OfficeService.java
│   └── VideoService.java
├── exception/               # Error handling
│   ├── custom_exception_classes/
│   ├── dto/
│   └── GlobalExceptionHandler.java
└── TheFileProjectApplication.java
```

## 🔧 Technology Stack

### Core Framework
- **Spring Boot**: 3.5.5
- **Java**: 24

### Image Processing
- **Thumbnailator**: 0.4.20 (resizing, compression)
- **WebP ImageIO**: 0.2.2 (WebP support)

### PDF Processing
- **Apache PDFBox**: 3.0.5
- **iText7**: 8.0.5 (core, kernel, layout)
- **html2pdf**: 4.0.5
- **JODConverter**: 4.4.6

### Office Document Processing
- **Apache POI**: 5.4.1 (poi, poi-ooxml, poi-scratchpad)
- **OpenSagres XDocReport**: 2.0.4 (DOCX to PDF conversion)

### Additional Libraries
- **OpenCSV**: 5.12.0 (CSV support)
- **Lombok**: Code generation
- **Spring Validation**: Input validation

## 🛡 Security Considerations

- **File Size Limits**: Configurable maximum upload size (default: 300MB)
- **File Type Validation**: Strict validation of input file types
- **Temporary File Cleanup**: Automatic cleanup of temporary files
- **Error Message Sanitization**: No sensitive information in error responses
- **Request Timeout Protection**: Prevents long-running requests from blocking resources
- **Input Validation**: Parameter validation using Spring Validation

## 🚧 Development Status

| Feature | Status | Notes |
|---------|--------|-------|
| **Image Processing** | ✅ Complete | All conversions, resizing, compression functional |
| **PDF Operations** | ✅ Complete | Basic conversions implemented |
| **Office Documents** | ✅ Complete | Cross-format conversions available |
| **Video Processing** | 🚧 Planned | Future implementation |

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 👥 Contributors

Developed by [Eren Tech Labs](https://github.com/erentechlabs)

## 🤝 Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## 📞 Support

For issues, questions, or suggestions, please open an issue on GitHub.


