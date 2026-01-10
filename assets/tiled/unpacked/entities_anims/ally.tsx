<?xml version="1.0" encoding="UTF-8"?>
<tileset version="1.10" tiledversion="1.11.2" name="ally" tilewidth="16" tileheight="16" tilecount="64" columns="8">
 <properties>
  <property name="attack1" value="square_attack"/>
  <property name="baseAttack" value="square_attack"/>
  <property name="hp" type="int" value="16"/>
  <property name="maxMoves" type="int" value="4"/>
  <property name="type" value="ally"/>
 </properties>
 <image source="../spritesheets/tileset_1bit.png" width="128" height="128"/>
 <tile id="0">
  <properties>
   <property name="animName" value="idle"/>
   <property name="fps" type="int" value="3"/>
   <property name="playMode" value="LOOP_PINGPONG"/>
  </properties>
 </tile>
 <tile id="38">
  <properties>
   <property name="animName" value="attack1"/>
   <property name="fps" type="int" value="11"/>
   <property name="playMode" value="LOOP"/>
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
