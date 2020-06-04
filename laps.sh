


CLASSPATH=""
for i in $(ls "./target/"*.jar)
do
	CLASSPATH="$CLASSPATH:$i"
done
for i in $(ls "./target/dependency/"*.jar)
do
	CLASSPATH="$CLASSPATH:$i"
done

echo "$CLASSPATH"

java -Xmx256M -cp "$CLASSPATH" Sandbox "$1" "$2"
