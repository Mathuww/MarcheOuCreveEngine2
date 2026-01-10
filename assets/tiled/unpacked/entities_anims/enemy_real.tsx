<?xml version="1.0" encoding="UTF-8"?>
<tileset version="1.10" tiledversion="1.11.2" name="enemy_real" tilewidth="32" tileheight="32" tilecount="56" columns="7">
 <grid orientation="orthogonal" width="16" height="16"/>
 <image source="../spritesheets/player_red.png" width="224" height="256"/>
 <tile id="6">
  <properties>
   <property name="animName" value="idle"/>
   <property name="fps" type="int" value="4"/>
   <property name="playMode" value="LOOP"/>
  </properties>
 </tile>
 <tile id="7">
  <properties>
   <property name="animName" value="death"/>
   <property name="fps" type="int" value="4"/>
   <property name="playMode" value="NORMAL"/>
  </properties>
 </tile>
 <tile id="18">
  <properties>
   <property name="animName" value="walk_down"/>
   <property name="fps" type="int" value="8"/>
   <property name="playMode" value="LOOP"/>
  </properties>
 </tile>
 <tile id="24">
  <properties>
   <property name="animName" value="walk_right"/>
   <property name="fps" type="int" value="8"/>
   <property name="playMode" value="LOOP"/>
  </properties>
 </tile>
 <tile id="30">
  <properties>
   <property name="animName" value="walk_up"/>
   <property name="fps" type="int" value="8"/>
   <property name="playMode" value="LOOP"/>
  </properties>
 </tile>
 <tile id="36">
  <properties>
   <property name="animName" value="attack1"/>
   <property name="fps" type="int" value="4"/>
   <property name="playMode" value="NORMAL"/>
  </properties>
 </tile>
 <tile id="48">
  <properties>
   <property name="animName" value="hurt"/>
   <property name="fps" type="int" value="4"/>
   <property name="playMode" value="LOOP"/>
  </properties>
 </tile>
</tileset>
