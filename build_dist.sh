rm -rf dist/
mkdir dist
sbt assembly
cp target/wikivector-assembly-1.0.0.jar dist/wikivector-dist.jar
cp bin/* dist/
