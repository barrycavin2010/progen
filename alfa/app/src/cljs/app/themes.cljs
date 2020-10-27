(ns alfa.themes)

(def first-theme
  {:palette {:textColor     "#343434"
             :primary1Color "#909090"}
   :spacing {:iconSize "40"}})

(def one ;cyan header
  {:palette {:primary1Color     "#0097A7"
             :primary2Color     "#009688"
             :primary3Color     "#B0BEC5"
             :accent1Color      "#ffcc01"
             :accent2Color      "#C1CDE6"
             :accent3Color      "#90A4AE"
             :borderColor       "#CFD8DC"
             :pickerHeaderColor "#0097A7"
             :clockCircleColor  "fade(#343434, 0.07)"}
   :spacing {:iconSize "40"}})

(def two ;BlueGrey header
  {:palette {:primary1Color     "#6C94B0"
             :primary2Color     "#009688"
             :primary3Color     "#B0BEC5"
             :accent1Color      "#ffcc01"
             :accent2Color      "#C1CDE6"
             :accent3Color      "#90A4AE"
             :borderColor       "#CFD8DC"
             :pickerHeaderColor "6C94B0"
             :clockCircleColor  "fade(#343434, 0.07)"}
   :spacing {:iconSize "40"}})

(def three ;BlueSyncano header
  {:palette {:primary1Color     "#0070d3"
             :primary2Color     "#009688"
             :primary3Color     "#B0BEC5"
             :accent1Color      "#ffcc01"
             :accent2Color      "#C1CDE6"
             :accent3Color      "#90A4AE"
             :borderColor       "#CFD8DC"
             :pickerHeaderColor "#0070d3"
             :clockCircleColor  "fade(#343434, 0.07)"}})

(def light-base-theme (aget js/MaterialUIStyles "lightBaseTheme"))
(def dark-base-theme (aget js/MaterialUIStyles "darkBaseTheme"))

(def zenius-theme three)
