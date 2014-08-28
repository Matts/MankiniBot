@echo off
title Push Up To Github.
set /p id= Commit Message:
echo %id%
git add --all
git commit -m %id%
git push
pause>null 
