#!/usr/bin/fish
# This script builds the frontend environment using fnm.

curl -fsSL https://fnm.vercel.app/install | bash
source /home/opencourse/.config/fish/conf.d/fnm.fish
fnm install 18.19.1
fnm default 18.19.1
fnm use 18.19.1
npm install
