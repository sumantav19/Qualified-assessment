(ns qualified-assessment.core-test
  (:require [clojure.test :refer :all]
            [qualified-assessment.core :as core]))

(deftest find-ingredients-test
  (testing "Given smoothie ordered name Classic it Returns * strawberry, banana, pineapple, mango, peach, honey, ice, yogurt*"
    (is (= " strawberry, banana, pineapple, mango, peach, honey, ice, yogurt" (core/find-ingredients "Classic"))))

  (testing "Given smoothie ordered name Mojito, which is  not available in menu  it Returns nil"
    (is (nil? (core/find-ingredients "Mojito")))))



(deftest remove-allergies-test
  (testing "Given smoothie ingredients * strawberry, banana, pineapple, mango, peach, honey, ice, yogurt* and allergies banana and mango it Returns ingredients without banana and mango"
    (is (= #{"honey" "ice" "peach" "pineapple" "strawberry" "yogurt"} (core/remove-allergies "strawberry, banana, pineapple, mango, peach, honey, ice, yogurt" #{"banana" "mango"}))))

  (testing "Given smoothie ingredients * strawberry, banana, pineapple, mango, peach, honey, ice, yogurt* and allergies empty list it Returns all  the ingredients"
    (is (= #{"banana" "honey" "ice" "mango" "peach" "pineapple" "strawberry" "yogurt"} (core/remove-allergies "strawberry, banana, pineapple, mango, peach, honey, ice, yogurt" #{})))))



(deftest get-ingredients-test
  (testing "Given order for smoothie *Classic,-strawberry* it filters strawberry and  Returns remaining ingredients"
    (is (=  #{"banana" "honey" "ice" "mango" "peach" "pineapple" "yogurt"} (core/get-ingredients "Classic,-strawberry"))))

  (testing "Given order for smoothie *Vegan Delite,-strawberry*  it filters strawberry and Returns remaining ingredients"
    (is (= #{"ice" "mango" "passion fruit" "peach" "pineapple" "soy milk"} (core/get-ingredients "Vegan Delite,-strawberry"))))

  (testing "Given order for smoothie *Vegan Delite,-banana*, banana does not exist in ingredients it Returns all ingredient"
    (is (= #{"ice" "mango" "passion fruit" "peach" "pineapple" "soy milk" "strawberry"} (core/get-ingredients "Vegan Delite,-banana"))))

  (testing "Given order for smoothie *Vegan Delite,mango* , mango doesn't have *-* suffix it Returns all ingredient"
    (is (= #{"ice" "mango" "passion fruit" "peach" "pineapple" "soy milk" "strawberry"} (core/get-ingredients "Vegan Delite,mango")))))



(deftest ingredients
  (testing "Classic smoothie"
    (is (= "banana,honey,ice,mango,peach,pineapple,strawberry,yogurt"
           (core/ingredients "Classic")))

    (testing "without strawberry"
      (is (= "banana,honey,ice,mango,peach,pineapple,yogurt"
             (core/ingredients "Classic,-strawberry")))))

  (testing "Just Desserts"
    (is (= "banana,cherry,chocolate,ice cream,peanut"
           (core/ingredients "Just Desserts")))

    (testing "without ice cream and peanut"
      (is (= "banana,cherry,chocolate"
             (core/ingredients "Just Desserts,-ice cream,-peanut")))))

  (testing "Random smoothie"
    (is (= "" (core/ingredients "Random,-ice cream")))))
