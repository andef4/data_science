unset key
set boxwidth 0.7
set style fill solid
set title 'term frequency distribution'
set xlabel 'term'
set ylabel 'frequency'
set xtics rotate
plot '-' using 2:xtic(1) with boxes
