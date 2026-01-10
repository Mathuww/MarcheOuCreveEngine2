<?xml version="1.0" encoding="UTF-8"?>
<tileset version="1.10" tiledversion="1.11.2" name="misc" tilewidth="16" tileheight="16" tilecount="64" columns="8">
 <image source="../spritesheets/tileset_1bit.png" width="128" height="128"/>
 <tile id="0">
  <properties>
   <property name="animName" value="idle"/>
   <property name="fps" type="int" value="4"/>
   <property name="playMode" value="LOOP_PINGPONG"/>
  </properties>
 </tile>
 <tile id="14">
  <properties>
   <property name="name" value="validAttackTile"/>
  </properties>
 </tile>
 <tile id="44">
  <properties>
   <property name="decreaseFactor" type="float" value="0.15"/>
   <property name="type" value="sender"/>
  </properties>
 </tile>
 <tile id="45">
  <properties>
   <property name="animName" value="walk"/>
   <property name="fps" type="int" value="6"/>
   <property name="playMode" value="LOOP"/>
  </properties>
 </tile>
 <tile id="57">
  <properties>
   <property name="type" value="attackDamage"/>
  </properties>
 </tile>
</tileset>
