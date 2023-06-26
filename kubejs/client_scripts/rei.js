// priority: 0
console.info('AQM3 -- Removing Items.... v1.0');
const removeJei = [
  "functionalstorage:ender_drawer",
  "projecte:rm_katar"
];

onEvent('rei.hide.items', event => {
  removeJei.forEach(item => {
    event.hide(item)
  })

})
