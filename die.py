#!/usr/bin/python3

import os
import shutil
import subprocess
import sys
from pathlib import Path

MAP_ROOT = Path("assets/tiled/unpacked")
PACK_OUTPUT_ROOT = Path("assets/tiled/packed")
PACKING_LOG = Path("misc/packing.log")
PACKER_JAR = Path("packing/runnable-tiledmappacker.jar")
BUILD_OUTPUT = Path("public/MarcheOuCreveEngine2.jar")

def usage():
    print("Usage : " + sys.argv[0] + " [pack|run]")
    sys.exit(1)

def main():
    print("--------------------------")
    print(" MARCHE OU CREVE ENGINE 2 ")
    print(" COPYRIGHT 2025           ")
    print("     ELOI RATHGEBER-KIVITS")
    print("     MATHEO DARNAUDGUILHEM")
    print("               GAIA DURAND")
    print("--------------------------\n")

    if len(sys.argv) < 2:
        print("MCE2 needs to be told what to do.")
        usage()
    if sys.argv[1] == "pack":
        pack()
    elif sys.argv[1] == "run":
        run()
    else:
        print("MCE2 doesn't know about the argument " + sys.argv[1])
        usage()
    
def pack():
    print("-- BUILDING YOUR GAME... --")
    print("Packing assets...")

    if PACK_OUTPUT_ROOT.exists():
        print(f"Cleaning {PACK_OUTPUT_ROOT}...")
        shutil.rmtree(PACK_OUTPUT_ROOT)
    
    if not os.path.exists("misc"):
        os.makedirs("misc")
        
    with open(PACKING_LOG, "w") as f:
        f.write("Packing log\n")

    print("Copying spritesheets & tilesets...")
    
    def ignore_patterns(path, names):
        return [n for n in names if n.endswith('.tmx') or n.endswith('.tiled-project') or n.endswith('.tiled-session')]

    shutil.copytree(MAP_ROOT, PACK_OUTPUT_ROOT, ignore=ignore_patterns)

    for root, dirs, files in os.walk(MAP_ROOT):
        has_tmx = any(f.endswith(".tmx") for f in files)
        
        if has_tmx:
            current_dir = Path(root)
            rel_path = current_dir.relative_to(MAP_ROOT)
            output_dir = PACK_OUTPUT_ROOT / rel_path
            
            print(f"Packing: {rel_path}")
            
            # Écriture dans le log
            with open(PACKING_LOG, "a") as log:
                log.write(f"Packing {rel_path}\n")

            cmd = [
                "java", "-jar", str(PACKER_JAR),
                str(current_dir),
                str(output_dir)
            ]

            is_linux = sys.platform.startswith('linux')
            no_display = os.environ.get('DISPLAY') is None
            
            has_xvfb = shutil.which("xvfb-run") is not None

            if is_linux and no_display:
                if has_xvfb:
                    cmd = ["xvfb-run", "-a"] + cmd
                else:
                    print("\n[WARNING] Headless Linux server detected, but 'xvfb-run' not installed.")
                    print("Packing will probably fail because TiledMapPacker needs a display server to run.")
            
            with open(PACKING_LOG, "a") as log:
                result = subprocess.run(cmd, stdout=log, stderr=log)
                
            if result.returncode != 0:
                print(f"ERROR while packing {rel_path}. See {PACKING_LOG}")

    print("\n-- Packing over. Starting Java build... -- ")

    is_windows = sys.platform.startswith('win')
    gradle_cmd = "mcgradlew.bat" if is_windows else "./mcgradlew"
    
    if is_windows and not os.path.exists("mcgradlew.bat"):
        gradle_cmd = "gradlew.bat"

    try:
        subprocess.run([gradle_cmd, "build", "--rerun-tasks"], check=True, shell=is_windows)
    except Exception as e:
        print(f"Error while building Java : {e}")
        sys.exit(1)
    
    print("\nGAME BUILT ! YOU CAN GO ENJOY IT.")

def run():
    print("-- RUNNING YOUR GAME... --")
    cmd = ["java", "-jar", str(BUILD_OUTPUT)]
    is_windows = sys.platform.startswith('win')
    try:
        subprocess.run(cmd, check=True, shell=is_windows)
    except Exception as e:
        print(f"Error while running the game : {e}")

if __name__ == "__main__":
    main()