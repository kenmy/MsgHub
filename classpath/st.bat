echo off
for /f "tokens=1,2,3,4 delims=/.- " %%i in ('date /t') do (
  for /f "tokens=1,2 delims=:. " %%p in ('time /t') do (
  set outfile=Log_%%l-%%j-%%k_%%p-%%q
))
echo.
echo outfile: %outfile%
echo.

java -cp F:\Data\Java\SkypeTwitter\skype-java-api-master\target\classes info.usbo.skypetwitter.Run > %outfile%.txt
