package com.bootstrap.auth.signIn

import com.bootstrap.R
import com.bootstrap.base.BaseFragment
import com.bootstrap.custom.viewBinding
import com.bootstrap.databinding.FragmentSignInBinding

class SignInFragment : BaseFragment<SignInViewModel>(R.layout.fragment_sign_in) {

    private val binding by viewBinding(FragmentSignInBinding::bind)

}