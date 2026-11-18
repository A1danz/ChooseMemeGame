FROM ubuntu:24.04
ENV DEBIAN_FRONTEND=noninteractive LANG=C.UTF-8 DISPLAY=:1
RUN apt-get update && apt-get install -y --no-install-recommends openjdk-21-jdk-headless openjfx xvfb x11vnc novnc websockify python3-pil fonts-dejavu-core fonts-noto-color-emoji && rm -rf /var/lib/apt/lists/*
WORKDIR /game
COPY src/main /src
COPY browser /browser
RUN mkdir classes && find /src/java -name '*.java' ! -name module-info.java > /tmp/sources && javac --module-path /usr/share/openjfx/lib --add-modules javafx.controls,javafx.fxml -d classes @/tmp/sources && cp -r /src/resources/. classes/ && python3 /browser/cards.py
RUN cp /browser/index.html /usr/share/novnc/index.html
USER 65534
ENTRYPOINT ["sh","/browser/start.sh"]
