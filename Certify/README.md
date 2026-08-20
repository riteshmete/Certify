# Certify - Bulk Certificate Generator

[![Java](https://img.shields.io/badge/Java-21-orange.svg?style=flat&logo=openjdk)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.x%20%2F%204.1.0-brightgreen.svg?style=flat&logo=springboot)](https://spring.io/projects/spring-boot)
[![Thymeleaf](https://img.shields.io/badge/Thymeleaf-3.x-green.svg?style=flat&logo=thymeleaf)](https://www.thymeleaf.org/)
[![Docker](https://img.shields.io/badge/Docker-Ready-blue.svg?style=flat&logo=docker)](https://www.docker.com/)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

**Certify** is an open-source, high-performance web application built with **Spring Boot**, **Thymeleaf**, and **Java 2D (AWT)** that empowers users to visually place text, customize typography, and generate hundreds of personalized high-resolution certificate images in seconds from a single template and CSV recipient list.

---

## Key Features

- **Interactive Visual Positioning**: Click directly on the live template canvas preview to position where recipient names will appear with precise X and Y coordinate tracking and guide markers.
- **Typography, Style & Color Customization**: Choose from embedded calligraphic script fonts (*Great Vibes*, *Alex Brush*, *Allura*, *Dancing Script*, *Cinzel*, etc.) or web standard fonts. Includes **Bold**, **Italic**, and custom **Text Color** pickers with quick preset swatches (Black, Gold, Navy, Maroon, Emerald, Charcoal) and adjustable font sizing (1-200 pt).
- **Live Typography Preview**: Interactive font dropdown where each font option is styled in its actual typeface, paired with a real-time live sample preview card showing how text will render.
- **Multi-Format Image Support**: Upload background certificate templates in `JPG`, `PNG`, `GIF`, `BMP`, or `WebP` formats (up to 10 MB).
- **Batch Processing via CSV**: Upload a CSV file containing up to 500 student/recipient names per batch.
- **Robust File Validation**: Includes MIME type and binary magic-byte header validation (`RIFF` WebP verification, image readability checking, and clean CSV parsing).
- **Automated ZIP Download**: Instantly bundles all generated individual PNG certificates into a single download stream (`certificates.zip`).
- **Docker Ready**: Includes a multi-stage `Dockerfile` (`eclipse-temurin:21`) for lightweight, cross-platform containerized execution.

---

## Tech Stack

| Layer | Technology |
| :--- | :--- |
| **Language** | Java 21 (LTS) |
| **Framework** | Spring Boot 3.4.x / Web MVC |
| **Template Engine** | Thymeleaf |
| **Image Processing** | Java 2D Graphics (`Graphics2D`, `BufferedImage`, `ImageIO`) |
| **Frontend Styling** | Modern Vanilla CSS3 (Custom properties, Glassmorphism, CSS Grid) |
| **Frontend Scripting**| Vanilla JavaScript (Canvas coordinate mapping & live crosshair guides) |
| **Build Tool** | Apache Maven |
| **Containerization**| Docker (Multi-stage build using Eclipse Temurin JDK/JRE 21) |

---

## Repository Structure

```
Certificate_genrator_Thymeleaf-/
└── Certify/
    ├── Dockerfile                     # Multi-stage Docker deployment config
    ├── mvnw / mvnw.cmd                # Maven Wrapper scripts
    ├── pom.xml                        # Maven dependencies & build setup
    └── src/
        └── main/
            ├── java/in/ac/project/certify/
            │   ├── CertifyApplication.java       # Main Spring Boot application entry point
            │   ├── controller/
            │   │   └── CertificateController.java # Handles form views & file download streams
            │   ├── model/
            │   │   └── CertificateContainer.java  # Form binding DTO
            │   ├── service/
            │   │   └── CertificateService.java    # Core rendering & ZIP archive logic
            │   └── util/
            │       └── FileValidator.java       # Magic-byte file integrity checks
            └── resources/
                ├── application.properties        # Application configuration
                ├── fonts/                        # Custom calligraphic TTF fonts
                │   ├── AlexBrush-Regular.ttf
                │   ├── Allura-Regular.ttf
                │   └── GreatVibes-Regular.ttf
                ├── static/css/
                │   └── style.css                 # Sleek UI design system
                └── templates/
                    ├── error.html
                    └── view_template.html        # Interactive main workspace
```

---

## CSV Format Requirement

Your uploaded CSV file must contain recipient names. The application parses the **first column** of each row (skipping the header line):

```csv
Name
John Doe
Jane Smith
Alex Johnson
Emily Davis
```

> **Note**: Empty rows are automatically ignored. Up to 500 recipient names can be processed per request.

---

## Quick Start Guide

### Prerequisites

- **Java 21 JDK** or higher installed
- **Git**
- **Docker** *(Optional, for containerized execution)*

### 1. Clone the Repository

```bash
git clone https://github.com/ritesh2996/Certificate_genrator_Thymeleaf-.git
cd Certificate_genrator_Thymeleaf-/Certify
```

### 2. Run Locally using Maven

You don't need to have Maven installed locally - use the included Maven wrapper (`./mvnw` or `mvnw.cmd`):

**On Linux/macOS:**
```bash
./mvnw clean spring-boot:run
```

**On Windows PowerShell / CMD:**
```cmd
.\mvnw.cmd clean spring-boot:run
```

Once started, open your web browser and navigate to:
`http://localhost:8080`

---

## Running with Docker

You can easily build and run Certify in a container:

### Build Docker Image
```bash
docker build -t certify-app .
```

### Run Container
```bash
docker run -d -p 8080:8080 --name certify certify-app
```

Access the app at `http://localhost:8080`.

---

## How It Works

1. **Upload Template**: Select your certificate background design image (`.png`, `.jpg`, `.webp`, etc.).
2. **Set Text Position**: Click anywhere on the rendered preview image to auto-fill the target **X** and **Y** pixel coordinates.
3. **Configure Typography**: Choose your font family (e.g., *Great Vibes*) and set font size in points.
4. **Upload CSV & Generate**: Upload your `.csv` file and click **Generate Certificates**. Your browser will prompt you to download `certificates.zip` containing all personalized certificate files!

---

## Configuration

Key configuration parameters defined in `application.properties`:

| Parameter | Default Value | Description |
| :--- | :--- | :--- |
| `server.port` | `8080` | Application HTTP Port (configurable via `${PORT}`) |
| `spring.servlet.multipart.max-file-size` | `50MB` | Max upload size per individual file |
| `spring.servlet.multipart.max-request-size` | `50MB` | Max total request size |

---

## Contributing

Contributions, issues, and feature requests are welcome!
Feel free to check the [issues page](https://github.com/ritesh2996/Certificate_genrator_Thymeleaf-/issues).

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

---

## License

Distributed under the **MIT License**. See `LICENSE` for more information.
