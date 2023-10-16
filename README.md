# PixelSorter
## Inspiration
Around early September 2023 I came across [a video by the youtuber Acerola](https://youtu.be/HMmmBDRy-jE?si=_dGWNU1vgg7iZf5r) about pixel sorters. 
It intrigued me, and over the next coming days I worked on my own implementation of the technique.

## Features
* 7+ Sorting Algorithms
  * Luminosity
  * Brightness
  * Hue
  * Saturation
  * Red Channel
  * Green Channel
  * Blue Channel
  * Green then Blue Channel Sort
    * Originally a bug, which I found cool and turned into a feature
* Thresholds
  * By setting a threshold (0-1 inclusive), any value below the lower threshold or above the upper threshold will not be processed by the algorithm.
  * Used to sort *some* of your image, but not all of it.
* Sorts both horizontally and vertically
  * Can be weighted so darker values are to the left, right, up, or down of the image.

## How to Use
1. Pull the source code to your device.
2. Change the variables above the Main() function to change the behaivor of the algorithm.
3. Change the file path on line 31 to the file path of the image to sort.
   * Supports JPEG, PNG, and GIF file formats. 
   * By default files are outputted in the /src/ folder with the title "final.png".
4. Run the program.