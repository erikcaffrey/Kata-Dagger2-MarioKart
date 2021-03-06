/*
 * Copyright (C) 2017 Erik Jhordan Rey.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.erikjhordanrey.kata_dagger2.view.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import io.github.erikjhordanrey.kata_dagger2.App
import io.github.erikjhordanrey.kata_dagger2.databinding.ActivityDetailCharacterBinding
import io.github.erikjhordanrey.kata_dagger2.domain.model.Character
import io.github.erikjhordanrey.kata_dagger2.view.adapter.CharacterDetailPagerAdapter
import io.github.erikjhordanrey.kata_dagger2.view.fragment.CharacterDetailFragment
import io.github.erikjhordanrey.kata_dagger2.view.fragment.CharacterFragment
import io.github.erikjhordanrey.kata_dagger2.view.presenter.CharactersPresenter
import javax.inject.Inject

class CharacterDetailActivity : AppCompatActivity(), CharactersPresenter.View {

    private lateinit var adapter: CharacterDetailPagerAdapter

    private lateinit var binding: ActivityDetailCharacterBinding

    @Inject
    lateinit var presenter: CharactersPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailCharacterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initializeDagger()
        initializeAdapter()
        presenter.view = this
        presenter.initialize()
    }


    override fun showCharacters(characters: List<Character>) {
        for (character in characters) {
            val characterFragment = CharacterDetailFragment.newInstance(character)
            adapter.addCharacter(characterFragment)
            adapter.notifyDataSetChanged()
        }

        binding.pager.run {
            currentItem = intent.getIntExtra(CharacterFragment.EXTRA_CHARACTER_POSITION, DEFAULT_POSITION)
            visibility = View.VISIBLE
        }
    }

    override fun hideLoading() {
        binding.pager.visibility = View.VISIBLE
        binding.progressCharacterDetail.visibility = View.GONE
    }

    private fun initializeDagger() {
        val app = application as App
        app.charactersComponent.inject(this)
    }

    private fun initializeAdapter() {
        adapter = CharacterDetailPagerAdapter(supportFragmentManager)
        binding.pager.offscreenPageLimit = 2
        binding.pager.adapter = adapter
    }

    companion object {

        private const val DEFAULT_POSITION = 0

        fun getCallingIntent(context: Context, position: Int): Intent {
            val intent = Intent(context, CharacterDetailActivity::class.java)
            intent.putExtra(CharacterFragment.EXTRA_CHARACTER_POSITION, position)
            return intent
        }
    }
}
