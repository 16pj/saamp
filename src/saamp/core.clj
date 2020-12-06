(ns saamp.core
  (:import (java.awt.event KeyEvent))
  (:use [quil.core :as q]))

(def food (atom #{}))

(defn partial-snake-head-boundary [range-val first-param second-param]
    "Given a range n and x and y, returns list of [[x-n/2 y] [x-n/2+1 y]...[x+n/2 y]]"
    (for [i (range (- (/ range-val 2)) (/ range-val 2))
      :let [first-prime1 (+ first-param i)]]
      [first-prime1 second-param]))

(defn get-meal [food snake-head]
(let [x (first snake-head)
      y (last snake-head)
    diff-size 16
    param1 (vec (partial-snake-head-boundary diff-size x y))
    param2 (vec (partial-snake-head-boundary diff-size y x))
    head-aura (vec (concat param1 param2))
    pots (filter #(get food %) head-aura)]
    (if (> (count pots) 0)
    (last pots))))

; (defn get-meal [food snake-bod]
;     (get @food (first snake-bod))
; )

(def snake-body (atom [[50 50]]))
(add-watch snake-body :key
  (fn [k r os nus]
    ; calculate region around head and if food is found then it's a meal
    (let [meal (get-meal @food (first nus))
         snake-head (first @snake-body)]
      (when meal
        ; remove food and add it to snake's head
        (swap! food disj meal)
        (swap! snake-body #(vec (cons meal %)))))))

(def snake-dir (atom [1 0]))

(def list-add (partial mapv +))

; on update function
(defn update-draw []
(let [new (list-add (first @snake-body) @snake-dir)]
    (swap! snake-body #(vec (cons new (pop %)))))

  (when (< (rand) 1/100)
    (swap! food conj [(rand-int 450) (rand-int 200)])))


; initial draw function
(defn draw []
  (q/background-float 0x20)  
  ; food
  (q/stroke 0xff)
  (doseq [[x y] @food ] (q/rect x y 5 5))

  ; snake
  (q/stroke 0x00 0xff 0xff)
  (doseq [[x y] @snake-body ] (q/rect x y 5 5)))

; input
(defn key-pressed []
  (cond
    (= (q/key-code) KeyEvent/VK_UP)
      (reset! snake-dir [0 -1])
    (= (q/key-code) KeyEvent/VK_DOWN)
      (reset! snake-dir [0 1])
    (= (q/key-code) KeyEvent/VK_LEFT)
      (reset! snake-dir [-1 0])
    (= (q/key-code) KeyEvent/VK_RIGHT)
      (reset! snake-dir [1 0])))


(defn -main
  "Run the game with lein run"
  [& args]
  ; run
  (q/defsketch snake
    :title "snake game thanks to noobtuts.com"
    :size [450 200]
    :setup (fn [] (q/smooth) (q/no-stroke) (q/frame-rate 60))
    :draw (fn [] (update-draw) (draw))
    :key-pressed key-pressed))