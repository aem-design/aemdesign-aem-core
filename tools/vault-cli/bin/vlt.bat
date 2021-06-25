@REM
@REM  Licensed to the Apache Software Foundation (ASF) under one or more
@REM  contributor license agreements.  See the NOTICE file distributed with
@REM  this work for additional information regarding copyright ownership.
@REM  The ASF licenses this file to You under the Apache License, Version 2.0
@REM  (the "License"); you may not use this file except in compliance with
@REM  the License.  You may obtain a copy of the License at
@REM
@REM       http://www.apache.org/licenses/LICENSE-2.0
@REM
@REM  Unless required by applicable law or agreed to in writing, software
@REM  distributed under the License is distributed on an "AS IS" BASIS,
@REM  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
@REM  See the License for the specific language governing permissions and
@REM  limitations under the License.

@REM ----------------------------------------------------------------------------
@REM Vault Start Up Batch script
@REM
@REM Required ENV vars:
@REM JAVA_HOME - location of a JDK home dir
@REM
@REM Optional ENV vars
@REM VLT_BATCH_ECHO - set to 'on' to enable the echoing of the batch commands
@REM VLT_BATCH_PAUSE - set to 'on' to wait for a key stroke before ending
@REM VLT_OPTS - parameters passed to the Java VM when running vlt
@REM     e.g. to debug vlt itself, use
@REM set VLT_OPTS=-Xdebug -Xnoagent -Djava.compiler=NONE -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=8000
@REM
@REM LINE SEPARATOR
@REM Because it can be pretty tricky to shove a newline character into a batch or shell script and the text "\n" doesn't get
@REM interpreted as what you might think by Java, vlt will instead look for LF | CRLF in a system property called vlt.line.separator
@REM and if it exists, it will set the appropriate Java System property accordingly, e.g. -Dvlt.line.separator=LF
@REM ----------------------------------------------------------------------------

@REM Begin all REM lines with '@' in case VLT_BATCH_ECHO is 'on'
@echo off
@REM enable echoing my setting VLT_BATCH_ECHO to 'on'
@if "%VLT_BATCH_ECHO%" == "on"  echo %VLT_BATCH_ECHO%

@REM set %HOME% to equivalent of $HOME
if "%HOME%" == "" (set HOME=%HOMEDRIVE%%HOMEPATH%)

@REM Execute a user defined script before this one
if exist "%HOME%\vltrc_pre.bat" call "%HOME%\vltrc_pre.bat"

set ERROR_CODE=0

:init
@REM Decide how to startup depending on the version of windows

@REM -- Win98ME
if NOT "%OS%"=="Windows_NT" goto Win9xArg

@REM set local scope for the variables with windows NT shell
if "%OS%"=="Windows_NT" @setlocal

@REM -- 4NT shell
if "%eval[2+2]" == "4" goto 4NTArgs

@REM -- Regular WinNT shell
set CMD_LINE_ARGS=%*
goto WinNTGetScriptDir

@REM The 4NT Shell from jp software
:4NTArgs
set CMD_LINE_ARGS=%$
goto WinNTGetScriptDir

:Win9xArg
@REM Slurp the command line arguments.  This loop allows for an unlimited number
@REM of arguments (up to the command line limit, anyway).
set CMD_LINE_ARGS=
:Win9xApp
if %1a==a goto Win9xGetScriptDir
set CMD_LINE_ARGS=%CMD_LINE_ARGS% %1
shift
goto Win9xApp

:Win9xGetScriptDir
set SAVEDIR=%CD%
%0\
cd %0\..\.. 
set VLT_HOME=%CD%
cd %SAVEDIR%
set SAVE_DIR=
goto repoSetup

:WinNTGetScriptDir
set VLT_HOME=%~dp0\..

:repoSetup


if "%JAVACMD%"=="" set JAVACMD=java

if "%REPO%"=="" set REPO=%VLT_HOME%\lib

set CLASSPATH="%REPO%"\org.apache.jackrabbit.vault-3.4.1-SNAPSHOT.jar;"%REPO%"\vault-vlt-3.4.1-SNAPSHOT.jar;"%REPO%"\jackrabbit-jcr-commons-2.19.6-SNAPSHOT.jar;"%REPO%"\vault-diff-3.4.1-SNAPSHOT.jar;"%REPO%"\diffutils-1.2.1.jar;"%REPO%"\commons-io-2.5.jar;"%REPO%"\vault-sync-3.4.1-SNAPSHOT.jar;"%REPO%"\commons-jci-fam-1.0.jar;"%REPO%"\commons-logging-api-1.1.jar;"%REPO%"\org.apache.sling.jcr.api-2.0.6.jar;"%REPO%"\org.apache.sling.commons.osgi-2.0.6.jar;"%REPO%"\vault-davex-3.4.1-SNAPSHOT.jar;"%REPO%"\jackrabbit-jcr-client-2.19.6-SNAPSHOT.jar;"%REPO%"\jackrabbit-spi-2.19.6-SNAPSHOT.jar;"%REPO%"\jackrabbit-spi-commons-2.19.6-SNAPSHOT.jar;"%REPO%"\commons-collections-3.2.2.jar;"%REPO%"\jackrabbit-jcr2spi-2.19.6-SNAPSHOT.jar;"%REPO%"\oak-jackrabbit-api-1.18.0.jar;"%REPO%"\jackrabbit-spi2dav-2.19.6-SNAPSHOT.jar;"%REPO%"\httpmime-4.5.3.jar;"%REPO%"\jackrabbit-webdav-2.19.6-SNAPSHOT.jar;"%REPO%"\httpcore-4.4.12.jar;"%REPO%"\httpclient-4.5.3.jar;"%REPO%"\commons-logging-1.0.3.jar;"%REPO%"\commons-codec-1.10.jar;"%REPO%"\jcl-over-slf4j-1.7.26.jar;"%REPO%"\commons-cli-2.0-mahout.jar;"%REPO%"\jline-0.9.94.jar;"%REPO%"\jcr-2.0.jar;"%REPO%"\slf4j-api-1.7.6.jar;"%REPO%"\slf4j-log4j12-1.7.6.jar;"%REPO%"\log4j-1.2.12.jar;"%REPO%"\vault-cli-3.4.1-SNAPSHOT.jar
goto endInit

@REM Reaching here means variables are defined and arguments have been captured
:endInit

%JAVACMD% %VLT_OPTS% -Xms500m -Xmx500m -classpath %CLASSPATH_PREFIX%;%CLASSPATH% -Dapp.name="vlt" -Dapp.repo="%REPO%" -Dapp.home="%VLT_HOME%" -Dvlt.home="%VLT_HOME%" org.apache.jackrabbit.vault.cli.VaultFsApp %CMD_LINE_ARGS%
if ERRORLEVEL 1 goto error
goto end

:error
if "%OS%"=="Windows_NT" @endlocal
set ERROR_CODE=%ERRORLEVEL%

:end
@REM set local scope for the variables with windows NT shell
if "%OS%"=="Windows_NT" goto endNT

@REM For old DOS remove the set variables from ENV - we assume they were not set
@REM before we started - at least we don't leave any baggage around
set CMD_LINE_ARGS=
goto postExec

:endNT
@REM If error code is set to 1 then the endlocal was done already in :error.
if %ERROR_CODE% EQU 0 @endlocal


:postExec
if exist "%HOME%\vltrc_post.bat" call "%HOME%\vltrc_post.bat"
@REM pause the batch file if VLT_BATCH_PAUSE is set to 'on'
if "%VLT_BATCH_PAUSE%" == "on" pause


if "%FORCE_EXIT_ON_ERROR%" == "on" (
  if %ERROR_CODE% NEQ 0 exit %ERROR_CODE%
)

exit /B %ERROR_CODE%
