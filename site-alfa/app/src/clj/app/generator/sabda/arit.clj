(ns app.generator.sabda.arit)

(def opsmap
  {"+" + "-" - "x" * "/" / "div" quot "mod" rem})

(defn arit-01
  []
  (-> (for [ops ["+" "-" "x"]
            b (range 3 20)
            a (range (+ b 1) 30)]
        (let [op (get opsmap ops)
              mapi {:a  a :b b :op ops
                    :pb (op a b)
                    :p1 (op (inc a) b)
                    :p2 (op (dec a) b)
                    :p3 (op a (inc b))
                    :p4 (op a (dec b))}]
          (condp = ops
            "+" (merge mapi
                       {:ch1 "Untuk jumlah satuan < 10 : 23 + 31 = (2+3) puluh + 4"
                        :ch2 "Puluhan dulu kerjain, baru tambahin jumlah satuan: 23 + 19 = 30 + 11 = 41"
                        :ch3 "Yang penting, mesti lancar aja jumlahin satuan yg hasilnya 10 < x < 20"
                        :ch4 "Kalo itu udah lancar mah, tinggal pisahin puluhan aja"})
            "-" (merge mapi
                       {:ch1 "Nah kalo satuan yg dikurangin > yg ngurang ya gampang ya, karena tinggal bikin aja puluhan-puluan and satuan - satuan"
                        :ch2 "Contoh 35 - 23 = [3-2][5-3]"
                        :ch3 "Kalo satuan yg dikurangin < yg ngurang, ambil jadi bentuk belasan"
                        :ch4 "Contoh 32 - 25 = ( 20 + 12 ) - ( 20 + 5 ) => 20nya abis, 12-5 = 7"
                        :ch5 "Contoh lagi 32 - 18 = (20 + 12) - (10 + 8) => (20-10) + (12-8)  = 14"
                        :ch6 "Inget ya, jangan diapal caranya, latihan temuin cara kreatif lo sendiri"})
            "x" mapi)))
      shuffle))

(defn arit-02
  []
  (-> (for [ops ["+" "-" "x"]
            b (range 2 20)
            a (range (+ b 2) 20)
            blank [:a :b :c]]
        (let [op (get opsmap ops)
              c (op a b)
              mapi (condp = blank
                     :a {:a "___" :b b :c c :op ops
                         :pb a :p1 (dec a) :p2 (inc a) :p3 (- a 2) :p4 (+ a 2)}
                     :b {:a a :b "___" :c c :op ops
                         :pb b :p1 (dec b) :p2 (inc b) :p3 (- b 2) :p4 (+ b 2)}
                     :c {:a a :b b :c "___" :op ops
                         :pb c
                         :p1 (op (dec a) b)
                         :p2 (op a (inc b))
                         :p3 (op (inc a) b)
                         :p4 (op (inc a) (inc b))})]
          (condp = ops
            "+" (merge mapi
                       {:ch1 "Untuk jumlah satuan < 10 : 23 + 31 = (2+3) puluh + 4"
                        :ch2 "Puluhan dulu kerjain, baru tambahin jumlah satuan: 23 + 19 = 30 + 11 = 41"
                        :ch3 "Yang penting, mesti lancar aja jumlahin satuan yg hasilnya 10 < x < 20"
                        :ch4 "Kalo itu udah lancar mah, tinggal pisahin puluhan aja"})
            "-" (merge mapi
                       {:ch1 "Nah kalo satuan yg dikurangin > yg ngurang ya gampang ya, karena tinggal bikin aja puluhan-puluan and satuan - satuan"
                        :ch2 "Contoh 35 - 23 = [3-2][5-3]"
                        :ch3 "Kalo satuan yg dikurangin < yg ngurang, ambil jadi bentuk belasan"
                        :ch4 "Contoh 32 - 25 = ( 20 + 12 ) - ( 20 + 5 ) => 20nya abis, 12-5 = 7"
                        :ch5 "Contoh lagi 32 - 18 = (20 + 12) - (10 + 8) => (20-10) + (12-8)  = 14"
                        :ch6 "Inget ya, jangan diapal caranya, latihan temuin cara kreatif lo sendiri"})
            "x" mapi)))
      shuffle))

(defn arit-03
  []
  (-> (for [b (range 2 50)
            a (range (+ b 2) 50)
            blank [:a :b :c]]
        (let [c (+ a b)
              ops "+"
              op +]
          (condp = blank
            :a {:a "___" :b b :c c :op ops
                :pb a :p1 (dec a) :p2 (inc a) :p3 (- a 2) :p4 (+ a 2)}
            :b {:a a :b "___" :c c :op ops
                :pb b :p1 (dec b) :p2 (inc b) :p3 (- b 2) :p4 (+ b 2)}
            :c {:a a :b b :c "___" :op ops
                :pb c
                :p1 (op (dec a) b)
                :p2 (op a (inc b))
                :p3 (op (inc a) b)
                :p4 (op (inc a) (inc b))})))
      shuffle))

(defn arit-04
  []
  (-> (for [b (range 2 50)
            a (range (+ b 2) 50)
            blank [:a :b :c]]
        (let [c (- a b)
              ops "-"
              op -]
          (condp = blank
            :a {:a "___" :b b :c c :op ops
                :pb a :p1 (dec a) :p2 (inc a) :p3 (- a 2) :p4 (+ a 2)}
            :b {:a a :b "___" :c c :op ops
                :pb b :p1 (dec b) :p2 (inc b) :p3 (- b 2) :p4 (+ b 2)}
            :c {:a a :b b :c "___" :op ops
                :pb c
                :p1 (op (dec a) b)
                :p2 (op a (inc b))
                :p3 (op (inc a) b)
                :p4 (op (inc a) (inc b))})))
      shuffle))


