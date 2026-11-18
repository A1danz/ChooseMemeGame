set -eu
base=ru.kpfu.itis.galeev.aidan.choosememegame
run() { java -Xmx192m -Dprism.order=sw --module-path /usr/share/openjfx/lib --add-modules javafx.controls,javafx.fxml -cp classes "$base.$1"; }
if [ "${1:-}" = server ]; then run testing.TestServer; exit; fi
Xvfb :1 -screen 0 900x600x24 -nolisten tcp &
while [ ! -S /tmp/.X11-unix/X1 ]; do sleep .1; done
x11vnc -display :1 -localhost -rfbport 5900 -forever -nevershared -dontdisconnect -nopw &
websockify --web=/usr/share/novnc 6080 localhost:5900 &
run MainApplication
