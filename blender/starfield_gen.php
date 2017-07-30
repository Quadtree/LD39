<?php 

$i = imagecreatetruecolor(1024, 1024);

for ($x=0;$x<1024;++$x){
	for ($y=0;$y<1024;++$y){
		if (mt_rand(0, 1000) == 0) imagesetpixel($i, $x, $y, imagecolorallocate($i, 255,255,255));
	}
}

imagepng($i, "starfield.png");