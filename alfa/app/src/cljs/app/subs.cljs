(ns alfa.subs
  (:require-macros [reagent.ratom :refer [reaction]])
  (:require [re-frame.core :as re]))

(re/register-sub
  :main-panel
  (fn [db]
    (reaction (:main-panel @db))))

(re/register-sub
  :active-cg
  (fn [db]
    (reaction (:active-cg @db))))

(re/register-sub
  :cg-children
  (fn [db]
    (reaction (:cg-children @db))))

(re/register-sub
  :containers
  (fn [db]
    (reaction (:containers @db))))

(re/register-sub
  :container
  (fn [db]
    (reaction (:container @db))))

(re/register-sub
  :playlist
  (fn [db]
    (reaction (:playlist @db))))

(re/register-sub
  :now-playing
  (fn [db]
    (reaction (:now-playing @db))))


