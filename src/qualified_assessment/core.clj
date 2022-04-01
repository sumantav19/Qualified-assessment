(ns qualified-assessment.core
  (:require [clojure.string :as string]))

(def smoothie-ingredients
  (list "Classic: strawberry, banana, pineapple, mango, peach, honey, ice, yogurt"
        "Forest Berry: strawberry, raspberry, blueberry, honey, ice, yogurt"
        "Freezie: blackberry, blueberry, black currant, grape juice, frozen yogurt"
        "Greenie: green apple, kiwi, lime, avocado, spinach, ice, apple juice"
        "Vegan Delite: strawberry, passion fruit, pineapple, mango, peach, ice, soy milk"
        "Just Desserts: banana, ice cream, chocolate, peanut, cherry"))

(def smoothies->ingredients (->> smoothie-ingredients
                                 (map #(string/split % #":"))
                                 (into {})))

(defn find-ingredients [smoothie-ordered]
  (get smoothies->ingredients smoothie-ordered))

(defn remove-allergies
  [ingredients allergies-set]
  (let [ingredients         (string/split ingredients #",")]
    (into (sorted-set) (comp (map string/trim)
                             (filter #(nil? (allergies-set %1)))) ingredients)))

(defn get-ingredients
  [order]
  (let [[smoothie-name & rest-order]     (string/split order #",")
        ingredients                      (find-ingredients smoothie-name)]
    (when ingredients
      (let [allergies                        (into #{}
                                                   (comp (filter #(string/includes? % "-"))
                                                         (map #(string/replace %1 #"-" "")))
                                                   rest-order)
            ingredients-w-allergies-removed  (remove-allergies ingredients allergies)]
        ingredients-w-allergies-removed))))

(defn ingredients [order]
  (cond
    (or
     (nil? order)
     (empty? order)) ""
    :else            (if-let [ingredients (get-ingredients order)]
                       (string/join "," ingredients)
                       "")))
