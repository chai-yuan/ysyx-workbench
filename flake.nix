{
  description = "A Nix-flake-based development environment";

  inputs = {
    nixpkgs.url = "github:nixos/nixpkgs/nixos-24.11";
    crPkgs.url = "github:chai-yuan/crpkgs";
  };

  outputs =
    {
      self,
      nixpkgs,
      crPkgs,
    }:
    let
      system = "x86_64-linux";
      pkgs = import nixpkgs { inherit system; };
      crpkgs = crPkgs.packages.${system};
    in
    {
      devShells."${system}".default = pkgs.mkShell {

        packages = [
          crpkgs.riscv64-elf-gcc
          pkgs.qemu
          pkgs.gcc
          pkgs.gnumake
          pkgs.bear
          pkgs.clang-tools
        ];

        shellHook = ''
          echo "A Nix-flake-based development environment"
          export AM_HOME=/home/charain/Project/ysyx-workbench/abstract-machine
          export CREMU_HOME=/home/charain/Project/RISCV-Emulator
        '';
      };
    };
}
