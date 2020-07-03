"""
dependencies.py
~~~~~~~~~~~~~~~

Download dependencies which can't be managed by Maven.
"""
from collections import namedtuple
from pathlib import Path
import subprocess
import urllib.request
import tempfile

MavenArtifact = namedtuple('MavenArtifact',
                           ['group_id', 'artifact_id', 'version'])


def install_repast_simphony_local_maven(version: str) -> None:
    artifact = MavenArtifact('io.github.repast',
                             'repast.simphony.bin_and_src',
                             version.lstrip('v'))
    with tempfile.TemporaryDirectory() as d:
        # import ipdb; ipdb.set_trace()
        local_jar = download_repast_jar(version, Path(d))
        install_jar_local_maven_repo(local_jar, artifact)


def download_repast_jar(version: str, out_dir: Path) -> Path:
    """Downloads Repast Simphony jar from GitHub to temporary directory.

    Parameters
    ----------
    version: Repast Simphony version, e.g. "v2.7.0"
    out_dir: Directory to write the downloaded jar to

    Returns
    -------
    Path to downloaded file
    """
    rs_url = (
        'https://github.com/Repast/repast.simphony/raw/'
        f'{version}/repast.simphony.bin_and_src/'
        'repast.simphony.bin_and_src.jar'
    )
    basename = Path(urllib.parse.urlparse(rs_url).path).name
    out_file = out_dir / basename

    with urllib.request.urlopen(rs_url) as r:
        with open(out_file, 'wb') as f:
            f.write(r.read())
    return out_file


def install_jar_local_maven_repo(jar: Path, artifact: MavenArtifact) -> None:
    subprocess.check_call(
        ['mvn', 'install:install-file',
         f'-Dfile={str(jar)}',
         f'-DgroupId={artifact.group_id}',
         f'-DartifactId={artifact.artifact_id}',
         f'-Dversion={artifact.version}',
         '-Dpackaging=jar']
    )

if __name__ == '__main__':
    install_repast_simphony_local_maven('v2.7.0')
