(ns qualified-assessment.core
  (:require [clojure.string :as string]))

(def smoothie-ingredients
  (list "Classic: strawberry, banana, pineapple, mango, peach, honey, ice, yogurt"
        "Forest Berry: strawberry, raspberry, blueberry, honey, ice, yogurt"
        "Freezie: blackberry, blueberry, black currant, grape juice, frozen yogurt"
        "Greenie: green apple, kiwi, lime, avocado, spinach, ice, apple juice"
        "Vegan Delite: strawberry, passion fruit, pineapple, mango, peach, ice, soy milk"
        "Just Desserts: banana, ice cream, chocolate, peanut, cherry"))

(defn find-ingredients [smoothie-ordered]
  (reduce (fn [_ smoothie-ingredient]
            (let [[smoothie-name ingredients] (-> smoothie-ingredient
                                                  (string/split #":"))]
              (when (= smoothie-ordered smoothie-name)
                (reduced ingredients))))
          nil smoothie-ingredients))

(defn remove-allergies
  [ingredients allergies-set]
  (let [ingredients         (string/split ingredients #",")]
    (into (sorted-set) (comp (map string/trim)
                             (filter #(nil? (allergies-set %1)))) ingredients)))

(defn get-ingredients
  [order]
  (let [[smoothie-name & rest-order]           (string/split order #",")
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
