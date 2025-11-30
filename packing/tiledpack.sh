#!/bin/bash

MAP_ROOT="assets/tiled/unpacked"
OUTPUT_ROOT="assets/tiled/packed"

rm -r assets/tiled/packed/** > /dev/null 2>&1
mkdir misc
rm misc/packing.log

rsync -av --exclude '*.tmx' --exclude '*.tiled-project' --exclude '*.tiled-session' "$MAP_ROOT"/ "$OUTPUT_ROOT"/

find "$MAP_ROOT" -type d | while read dir; do
    # si ce dossier contient des .tmx directement
    if ls "$dir"/*.tmx >/dev/null 2>&1; then
        
        # chemin relatif
        rel="${dir#"$MAP_ROOT"/}"

        echo "Packing $rel"
        echo "Packing $rel" >> misc/packing.log

        xvfb-run -a \
            java -jar packing/runnable-tiledmappacker.jar \
            "$dir" \
            "$OUTPUT_ROOT/$rel" \
            2>> misc/packing.log
        
        echo ""
    fi
done

./mcgradlew build --rerun-tasks
