BAR Map Studio - Linux and macOS package

NOT TESTED. This package was prepared together with the Windows version and has never been run
on Linux or macOS. Please report what does not work.

Requirements
- Java 25 or newer ("java" on the PATH, or the JAVA_HOME variable).
  On Linux it must be the full package, not the "headless" one, which cannot open windows:
      Debian / Ubuntu / Mint:  sudo apt install openjdk-25-jre
      Fedora:                  sudo dnf install java-25-openjdk
      Arch:                    sudo pacman -S jre-openjdk
  The error "HeadlessException: No X11 DISPLAY variable was set, or no headful library support was found"
  means exactly this, or that the program was started outside the desktop session (text console, SSH).
  On Wayland, XWayland must be installed.
- Beyond All Reason installed and started once.

Start
    sh bar-map-studio.sh

If Beyond All Reason is not found by itself, press the BAR button at the bottom and pick its data folder
(Linux: ~/.local/state/Beyond-All-Reason, Flatpak: ~/.var/app/info.beyondallreason.bar/.local/state/Beyond-All-Reason,
macOS: ~/Library/Application Support/Beyond-All-Reason-mac).

Settings, cache and logs are kept next to the program when that folder can be written, otherwise in
~/.config/BARMapStudio (Linux) or ~/Library/Application Support/BARMapStudio (macOS).
