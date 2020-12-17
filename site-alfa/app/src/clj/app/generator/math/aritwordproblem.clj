(ns app.generator.math.regis
  (:require [app.generator.math.arit]))

(defn logic-01
  []
  (for [n1 ["Pak Santo" "Bu Dian" "Pak Brian" "Bu Titi" "Pak Laude" "Bu Krista"]
        n2 ["sapi" "kuda" "kambing" "domba" "kerbau"]
		k1 [1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 18 19 20]
		k2 [1 2 3 4 5 6 7 8 9 10]
		k3 ["banyak" "sedikit"]]
	{:n1 n1 :n2 n2 :k1 k1 :k2 k2 :k3 k3}
	(if (= "banyak")
		{
		:pb (/(k2)(- k1 k4))
		:p1 (/(k2)(- k4 k1))
		:p2 (/(-k2)(- k4 k1))
		:p3 (/(-k2)(- k1 k4))
		:k4 "+"
		}
		
		{
		:pb (/(-k2)(- k1 k4))
		:p1 (/(k2)(- k1 k4))
		:p2 (/(-k2)(- k4 k1))
		:p3 (/(k2)(- k4 k1))0
		:k4 "-"
		}
	)




