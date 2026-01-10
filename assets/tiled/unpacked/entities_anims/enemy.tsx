<?xml version="1.0" encoding="UTF-8"?>
<tileset version="1.10" tiledversion="1.11.2" name="enemy" tilewidth="16" tileheight="16" tilecount="64" columns="8">
 <image source="../spritesheets/tileset_1bit.png" width="128" height="128"/>
 <tile id="12">
  <properties>
   <property name="animName" value="idle"/>
   <property name="fps" type="int" value="2"/>
   <property name="playMode" value="LOOP"/>
  </properties>
 </tile>
 <tile id="23">
  <properties>
   <property name="animName" value="dead"/>
   <property name="fps" type="int" value="2"/>
   <property name="playMode" value="NORMAL"/>
  </properties>
 </tile>
 <tile id="39">
  <properties>
   <property name="animName" value="attack1"/>
   <property name="fps" type="int" value="2"/>
   <property name="playMode" value="LOOP"/>
  </properties>
 </tile>
 <tile id="54">
  <properties>
   <property name="animName" value="walk"/>
   <property name="fps" type="int" value="2"/>
   <property name="playMode" value="LOOP"/>
  </properties>
 </tile>
 <tile id="61">
  <properties>
   <property name="animName" value="hurt"/>
   <property name="fps" type="int" value="2"/>
   <property name="playMode" value="NORMAL"/>
   <property name="trigger" value="idle"/>
  </properties>
 </tile>
</tileset>
