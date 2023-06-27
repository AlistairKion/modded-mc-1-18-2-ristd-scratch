// priority: 0
console.info('AQM3 -- Removing Items.... v1.0');
const removeJei = [
  "functionalstorage:ender_drawer",
	"projecte:rm_katar",
	"projecte:rm_morning_star",
	"projecte:rm_sword",
	"projecte:rm_hammer",
	"projecte:gem_helmet",
	"projecte:gem_chestplate",
	"projecte:gem_leggings",
	"projecte:gem_boots",
	"projecte:swiftwolf_rending_gale",
	"projecte:archangel_smite",
	"projecte:arcana_ring",
	"projecte:body_stone",
	"projecte:soul_stone",
	"projecte:mind_stone",
	"projecte:life_stone",
	"projecte:destruction_catalyst",
	"projecte:hyperkinetic_lens",
	"projecte:catalytic_lens"
];

onEvent('rei.hide.items', event => {
  removeJei.forEach(item => {
    event.hide(item)
  })
})
